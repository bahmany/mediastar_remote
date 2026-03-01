/**
 * STB Management Application
 * Complete integration with Python backend WebSocket API
 */

class STBApp {
    constructor() {
        this.ws = null;
        this.wsConnected = false;
        this.stbConnected = false;
        this.wsState = 'disconnected';
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 10;
        this.reconnectDelay = 1000;
        this.heartbeatInterval = null;
        this.reconnectTimeout = null;
        this.isReconnecting = false;
        
        this.state = {
            channels: [],
            favorites: [],
            favoriteGroups: [],
            currentChannel: null,
            cacheInfo: null,
            loginInfo: null
        };
        this.currentTab = 'all';
        this.searchTerm = '';
        this.messageId = 0;
        this.pendingRequests = new Map();

        this.channelsLoading = false;
        this.channelsExpected = null;
        this.channelsMap = new Map();

        this.discoveredDevices = [];
        
        this.init();
    }

    init() {
        this.setupWebSocket();
        this.setupEventListeners();
        this.loadStbTargetFromStorage();
        this.updateDiscoveredSelect();
        this.setupPageVisibilityHandling();
        this.updateUI();
    }

    updateDiscoveredSelect() {
        const sel = document.getElementById('discoveredSelect');
        if (!sel) {
            return;
        }

        const devices = Array.isArray(this.discoveredDevices) ? this.discoveredDevices : [];
        sel.innerHTML = '';

        const placeholder = document.createElement('option');
        placeholder.value = '';
        placeholder.textContent = devices.length ? 'Select device…' : 'No devices';
        sel.appendChild(placeholder);

        devices.forEach((d, idx) => {
            const ip = String(d?.ip || '');
            const model = String(d?.model_name || '').trim();
            const serial = String(d?.serial || '').trim();

            const opt = document.createElement('option');
            opt.value = String(idx);
            opt.textContent = `${ip}${model ? ' • ' + model : ''}${serial ? ' • ' + serial : ''}`;
            sel.appendChild(opt);
        });

        sel.disabled = !this.wsConnected || devices.length === 0;
    }

    discoverStb(timeout = 5.0) {
        this.sendMessage(
            'discover_stb',
            { timeout: Number(timeout) || 5.0 },
            (data) => {
                const devices = Array.isArray(data?.devices) ? data.devices : [];
                this.discoveredDevices = devices;
                this.updateDiscoveredSelect();
                this.showNotification(`Found ${devices.length} device(s)`, 'info');
            },
            (error) => {
                this.showNotification(`Discovery error: ${error}`, 'error');
            }
        );
    }

    loadStbTargetFromStorage() {
        const ipEl = document.getElementById('stbIp');
        const portEl = document.getElementById('stbPort');
        if (!ipEl || !portEl) {
            return;
        }

        const savedIp = localStorage.getItem('stb_ip');
        const savedPort = localStorage.getItem('stb_port');

        if (savedIp) {
            ipEl.value = savedIp;
        }
        if (savedPort && !Number.isNaN(Number(savedPort))) {
            portEl.value = String(Number(savedPort));
        }
    }

    saveStbTargetToStorage(ip, port) {
        try {
            localStorage.setItem('stb_ip', String(ip || ''));
            localStorage.setItem('stb_port', String(Number(port || 20000)));
        } catch {
            // ignore
        }
    }

    setChannelsLoading(loading, opts = {}) {
        this.channelsLoading = Boolean(loading);
        const wrap = document.getElementById('channelsProgressWrap');
        const text = document.getElementById('channelsProgressText');
        const count = document.getElementById('channelsProgressCount');
        const bar = document.getElementById('channelsProgressBar');

        if (!wrap || !text || !count || !bar) {
            return;
        }

        if (!this.channelsLoading) {
            wrap.classList.add('d-none');
            bar.style.width = '0%';
            bar.setAttribute('aria-valuenow', '0');
            bar.classList.remove('progress-bar-striped', 'progress-bar-animated');
            return;
        }

        wrap.classList.remove('d-none');
        text.textContent = opts.text || 'Loading channels…';

        const cur = Number.isFinite(opts.current) ? opts.current : 0;
        const exp = Number.isFinite(opts.expected) ? opts.expected : (Number.isFinite(this.channelsExpected) ? this.channelsExpected : 0);

        if (exp > 0) {
            const pct = Math.max(0, Math.min(100, Math.round((cur / exp) * 100)));
            count.textContent = `${cur}/${exp}`;
            bar.classList.remove('progress-bar-striped', 'progress-bar-animated');
            bar.style.width = `${pct}%`;
            bar.setAttribute('aria-valuenow', String(pct));
        } else {
            count.textContent = `${cur}`;
            bar.classList.add('progress-bar-striped', 'progress-bar-animated');
            bar.style.width = '100%';
            bar.setAttribute('aria-valuenow', '100');
        }
    }

    setupPageVisibilityHandling() {
        // Handle page visibility changes to prevent unnecessary reconnections
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                // Page is hidden, reduce heartbeat frequency
                this.stopHeartbeat();
            } else {
                // Page is visible again, restore normal heartbeat
                if (this.wsConnected && this.ws && this.ws.readyState === WebSocket.OPEN) {
                    this.startHeartbeat();
                } else if (!this.isReconnecting) {
                    // Try to reconnect if connection was lost while hidden
                    this.attemptReconnect();
                }
            }
        });

        // Handle page unload to properly close connection
        window.addEventListener('beforeunload', () => {
            this.closeConnection();
        });

        // Handle network status changes
        window.addEventListener('online', () => {
            console.log('Network back online');
            if (!this.wsConnected && !this.isReconnecting) {
                this.showNotification('Network restored, reconnecting...', 'info');
                this.attemptReconnect();
            }
        });

        window.addEventListener('offline', () => {
            console.log('Network offline');
            this.showNotification('Network connection lost', 'warning');
            this.stopHeartbeat();
        });
    }

    setupWebSocket() {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            return; // Already connected
        }

        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws`;
        
        console.log('Connecting to WebSocket:', wsUrl);
        this.wsState = 'connecting';
        this.wsConnected = false;
        this.updateConnectionStatus();
        this.updateConnectionInfo(this.state.loginInfo);
        
        this.ws = new WebSocket(wsUrl);
        
        this.ws.onopen = () => {
            console.log('WebSocket connected successfully');
            this.wsConnected = true;
            this.wsState = 'connected';
            this.reconnectAttempts = 0;
            this.isReconnecting = false;
            
            this.updateConnectionStatus();
            this.startHeartbeat();
            
            // Clear any pending reconnection
            if (this.reconnectTimeout) {
                clearTimeout(this.reconnectTimeout);
                this.reconnectTimeout = null;
            }
            
            this.showNotification('WebSocket connected', 'success');
        };
        
        this.ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                this.handleMessage(data);
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        };
        
        this.ws.onclose = (event) => {
            console.log('WebSocket closed:', event.code, event.reason);
            this.wsConnected = false;
            this.wsState = 'disconnected';
            this.stopHeartbeat();
            this.updateConnectionStatus();
            
            // Don't reconnect if it was a normal closure or if we're already reconnecting
            if (event.code === 1000 || this.isReconnecting) {
                return;
            }
            
            this.attemptReconnect();
        };
        
        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.wsConnected = false;
            this.wsState = 'disconnected';
            this.stopHeartbeat();
            
            if (!this.isReconnecting) {
                this.showNotification('Connection error', 'error');
            }
        };
    }

    startHeartbeat() {
        this.stopHeartbeat(); // Clear any existing heartbeat
        
        // Don't start heartbeat if page is hidden
        if (document.hidden) {
            return;
        }
        
        this.heartbeatInterval = setInterval(() => {
            if (this.ws && this.ws.readyState === WebSocket.OPEN && !document.hidden) {
                // Send a ping message to keep connection alive
                try {
                    this.ws.send(JSON.stringify({
                        type: 'ping',
                        payload: { timestamp: Date.now() },
                        id: ++this.messageId
                    }));
                } catch (error) {
                    console.error('Failed to send heartbeat:', error);
                    this.stopHeartbeat();
                    this.attemptReconnect();
                }
            } else if (this.ws && this.ws.readyState !== WebSocket.OPEN) {
                this.stopHeartbeat();
                if (!this.isReconnecting && !document.hidden) {
                    this.attemptReconnect();
                }
            }
        }, 25000); // Send heartbeat every 25 seconds
    }

    stopHeartbeat() {
        if (this.heartbeatInterval) {
            clearInterval(this.heartbeatInterval);
            this.heartbeatInterval = null;
        }
    }

    attemptReconnect() {
        // Don't reconnect if page is hidden or offline
        if (document.hidden || !navigator.onLine) {
            return;
        }

        if (this.isReconnecting || this.reconnectAttempts >= this.maxReconnectAttempts) {
            if (this.reconnectAttempts >= this.maxReconnectAttempts) {
                this.showNotification('Max reconnection attempts reached. Please refresh the page.', 'error');
                this.wsState = 'disconnected';
                this.wsConnected = false;
                this.updateConnectionStatus();
            }
            return;
        }

        this.isReconnecting = true;
        this.reconnectAttempts++;
        
        // Exponential backoff with jitter
        const baseDelay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
        const jitter = Math.random() * 1000; // Add up to 1 second of jitter
        const delay = Math.min(baseDelay + jitter, 30000);
        
        console.log(`Reconnection attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts} in ${Math.round(delay)}ms`);
        this.wsState = 'reconnecting';
        this.wsConnected = false;
        this.updateConnectionStatus();
        
        this.reconnectTimeout = setTimeout(() => {
            if (!document.hidden && navigator.onLine) {
                this.isReconnecting = false;
                this.setupWebSocket();
            } else {
                // Conditions changed, abort this reconnection attempt
                this.isReconnecting = false;
                this.reconnectAttempts--;
            }
        }, delay);
    }

    closeConnection() {
        this.stopHeartbeat();
        
        if (this.reconnectTimeout) {
            clearTimeout(this.reconnectTimeout);
            this.reconnectTimeout = null;
        }
        
        if (this.ws) {
            this.ws.close(1000, 'Normal closure');
            this.ws = null;
        }
        
        this.wsConnected = false;
        this.wsState = 'disconnected';
        this.isReconnecting = false;
        this.reconnectAttempts = 0;
    }

    handleMessage(data) {
        if (!data?.event || (data.event !== 'channel_upsert' && data.event !== 'channel_list_progress')) {
            console.log('Received message:', data);
        }
        
        if (data.type === 'snapshot') {
            this.handleSnapshot(data);
        } else if (data.type === 'response') {
            this.handleResponse(data);
        } else if (data.type === 'notification') {
            this.showNotification(data.message, data.level);
        } else if (data.event) {
            this.handleEvent(data);
        }
        
        // Handle pong responses (heartbeat acknowledgment)
        if (data.ok && data.data && data.data.type === 'pong') {
            console.log('Received pong response, connection is alive');
        }
    }

    handleSnapshot(data) {
        this.stbConnected = Boolean(data.connected);
        this.state.loginInfo = data.login_info;
        
        if (data.state) {
            this.state.channels = data.state.channels || [];
            this.state.currentChannel = data.state.current_channel_index;
            this.state.cacheInfo = data.state.cache_info;

            const chNum = data.state?.stb_info?.ChannelNum;
            if (chNum !== undefined && chNum !== null && !Number.isNaN(Number(chNum))) {
                this.channelsExpected = Number(chNum);
            }

            if (Array.isArray(this.state.channels) && this.state.channels.length > 0) {
                this.channelsMap = new Map();
                for (const ch of this.state.channels) {
                    const idx = ch?.ServiceIndex ?? ch?.ProgramIndex;
                    if (idx === undefined || idx === null) {
                        continue;
                    }
                    this.channelsMap.set(Number(idx), ch);
                }
                this.setChannelsLoading(false);
            }
            
            if (data.state.favorites_summary) {
                this.state.favoriteGroups = data.state.favorites_summary.groups || [];
                this.updateFavoritesCount();
            }
        }
        
        this.updateConnectionStatus();
        this.updateConnectionInfo(this.state.loginInfo);
        
        this.updateUI();
    }

    handleResponse(data) {
        const request = this.pendingRequests.get(data.id);
        if (request) {
            this.pendingRequests.delete(data.id);
            
            if (data.ok) {
                if (request.onSuccess) {
                    request.onSuccess(data.data);
                }
            } else {
                if (request.onError) {
                    request.onError(data.error);
                }
                this.showNotification(data.error || 'Operation failed', 'error');
            }
        }
    }

    handleEvent(data) {
        if (data.event === 'disconnected') {
            this.showNotification('STB connection lost. Attempting to reconnect...', 'warning');
        } else if (data.event === 'reconnecting') {
            const attempt = data.data?.attempt || 1;
            const maxAttempts = data.data?.max_attempts || 5;
            this.showNotification(`Reconnecting... (${attempt}/${maxAttempts})`, 'info');
        } else if (data.event === 'reconnected') {
            this.showNotification('Reconnected successfully!', 'success');
        } else if (data.event === 'reconnect_failed') {
            this.showNotification('Reconnection failed. Please connect manually.', 'error');
        } else if (data.event === 'stb_info') {
            const chNum = data.data?.ChannelNum;
            if (chNum !== undefined && chNum !== null && !Number.isNaN(Number(chNum))) {
                this.channelsExpected = Number(chNum);
                if (this.channelsLoading) {
                    this.setChannelsLoading(true, { current: this.channelsMap.size, expected: this.channelsExpected });
                }
            }
        } else if (data.event === 'channel_list_reset') {
            this.channelsMap = new Map();
            this.state.channels = [];
            this.setChannelsLoading(true, { text: 'Loading channels…', current: 0, expected: this.channelsExpected || 0 });
            this.updateConnectionStatus();
            this.updateChannelList();
        } else if (data.event === 'channel_upsert') {
            const ch = data.data;
            const idx = ch?.ServiceIndex ?? ch?.ProgramIndex;
            if (idx !== undefined && idx !== null) {
                this.channelsMap.set(Number(idx), ch);
            }
        } else if (data.event === 'channel_list_progress') {
            const count = typeof data.data?.count === 'number' ? data.data.count : this.channelsMap.size;
            this.setChannelsLoading(true, { text: 'Loading channels…', current: count, expected: this.channelsExpected || 0 });
        } else if (data.event === 'channel_list_complete') {
            const finalCount = typeof data.data?.count === 'number' ? data.data.count : this.channelsMap.size;
            this.setChannelsLoading(false);

            const channels = Array.from(this.channelsMap.entries())
                .sort((a, b) => a[0] - b[0])
                .map(([, v]) => v);

            this.state.channels = channels;
            this.showNotification(`Channels loaded: ${finalCount}`, 'success');
            this.updateUI();
            this.updateConnectionStatus();

            this.sendMessage('get_state', {});
        }
    }

    sendMessage(type, payload = {}, onSuccess = null, onError = null) {
        if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
            this.showNotification('WebSocket not connected', 'error');
            // Try to reconnect if not already trying
            if (!this.isReconnecting && navigator.onLine && !document.hidden) {
                this.attemptReconnect();
            }
            return;
        }

        const id = ++this.messageId;
        const message = { type, payload, id };
        
        if (onSuccess || onError) {
            this.pendingRequests.set(id, { onSuccess, onError });
        }
        
        try {
            this.ws.send(JSON.stringify(message));
        } catch (error) {
            console.error('Failed to send message:', error);
            this.showNotification('Failed to send message', 'error');
            if (!this.isReconnecting) {
                this.attemptReconnect();
            }
        }
        
        return id;
    }

    // Add manual reconnect method
    manualReconnect() {
        console.log('Manual reconnect requested');
        this.closeConnection();
        this.reconnectAttempts = 0; // Reset attempts for manual reconnect
        this.setupWebSocket();
    }

    setupEventListeners() {
        // Connection controls
        document.getElementById('connectBtn').addEventListener('click', () => {
            this.connectToSTB();
        });
        
        document.getElementById('disconnectBtn').addEventListener('click', () => {
            this.disconnectFromSTB();
        });
        
        // Channel controls
        document.getElementById('refreshChannelsBtn').addEventListener('click', () => {
            this.refreshChannels(true);
        });

        const discoverBtn = document.getElementById('discoverBtn');
        if (discoverBtn) {
            discoverBtn.addEventListener('click', () => {
                this.discoverStb(5.0);
            });
        }

        const discoveredSelect = document.getElementById('discoveredSelect');
        if (discoveredSelect) {
            discoveredSelect.addEventListener('change', () => {
                if (!discoveredSelect.value) {
                    return;
                }
                const idx = Number(discoveredSelect.value);
                if (!Number.isFinite(idx) || idx < 0) {
                    return;
                }
                const d = this.discoveredDevices?.[idx];
                const ip = String(d?.ip || '');
                if (!ip) {
                    return;
                }
                const ipEl = document.getElementById('stbIp');
                const portEl = document.getElementById('stbPort');
                if (ipEl) {
                    ipEl.value = ip;
                }
                if (portEl) {
                    portEl.value = '20000';
                }
                this.saveStbTargetToStorage(ip, 20000);
            });
        }
        
        // Tab switching
        document.querySelectorAll('.tab').forEach(tab => {
            tab.addEventListener('click', () => {
                this.switchTab(tab.dataset.tab);
            });
        });
        
        // Search
        document.getElementById('searchInput').addEventListener('input', (e) => {
            this.searchTerm = e.target.value;
            this.updateChannelList();
        });
        
        // Remote control
        document.querySelectorAll('[data-key]').forEach(btn => {
            btn.addEventListener('click', () => {
                this.sendRemoteKey(btn.dataset.key);
            });
        });
        
        // Favorites management
        document.getElementById('addGroupBtn').addEventListener('click', () => {
            this.showAddGroupDialog();
        });
    }

    connectToSTB() {
        const ip = document.getElementById('stbIp').value;
        const port = parseInt(document.getElementById('stbPort').value);
        
        if (!ip) {
            this.showNotification('Please enter IP address', 'error');
            return;
        }

        this.saveStbTargetToStorage(ip, port);
        
        this.sendMessage('connect', { ip, port }, 
            (data) => {
                this.showNotification('Connected successfully', 'success');
                this.updateConnectionInfo(data.login_info);
            },
            (error) => {
                this.showNotification(`Connection error: ${error}`, 'error');
            }
        );
    }

    disconnectFromSTB() {
        this.sendMessage('disconnect', {}, 
            () => {
                this.showNotification('Disconnected', 'info');
            }
        );
    }

    refreshChannels(force = false) {
        if (force) {
            this.channelsMap = new Map();
            this.state.channels = [];
            this.setChannelsLoading(true, { text: 'Refreshing channels…', current: 0, expected: this.channelsExpected || 0 });
            this.updateUI();
        }

        this.sendMessage('refresh_channels', { force }, 
            (data) => {
                this.showNotification(`${data.channel_count} channels loaded`, 'success');
            },
            (error) => {
                this.showNotification(`Error loading channels: ${error}`, 'error');
                if (force) {
                    this.setChannelsLoading(false);
                    this.updateConnectionStatus();
                    this.updateUI();
                }
            }
        );
    }

    switchTab(tab) {
        this.currentTab = tab;
        
        // Update tab appearance
        document.querySelectorAll('.tab').forEach(t => {
            t.classList.toggle('active', t.dataset.tab === tab);
        });
        
        this.updateChannelList();
    }

    updateConnectionStatus() {
        const statusDot = document.getElementById('statusDot');
        const statusText = document.getElementById('statusText');
        const reconnectBtn = document.getElementById('reconnectBtn');
        const connectBtn = document.getElementById('connectBtn');
        const disconnectBtn = document.getElementById('disconnectBtn');
        const refreshChannelsBtn = document.getElementById('refreshChannelsBtn');
        const addGroupBtn = document.getElementById('addGroupBtn');
        const discoverBtn = document.getElementById('discoverBtn');
        const discoveredSelect = document.getElementById('discoveredSelect');

        if (!statusDot || !statusText || !connectBtn || !disconnectBtn) {
            return;
        }

        // Header status reflects BOTH WebSocket and STB.
        if (!this.wsConnected) {
            statusDot.className = 'status-dot disconnected';
            statusText.textContent = this.wsState === 'reconnecting' ? 'Reconnecting...' : 'WebSocket disconnected';
        } else if (this.stbConnected) {
            statusDot.className = 'status-dot connected';
            statusText.textContent = 'STB connected';
        } else {
            statusDot.className = 'status-dot connecting';
            statusText.textContent = 'WebSocket connected / STB disconnected';
        }

        // STB connect/disconnect buttons depend on STB state,
        // but are disabled if WebSocket is not connected.
        connectBtn.style.display = this.stbConnected ? 'none' : 'inline-flex';
        disconnectBtn.style.display = this.stbConnected ? 'inline-flex' : 'none';
        connectBtn.disabled = !this.wsConnected;
        disconnectBtn.disabled = !this.wsConnected;

        if (refreshChannelsBtn) {
            refreshChannelsBtn.disabled = !this.wsConnected || !this.stbConnected || this.channelsLoading;
        }
        if (addGroupBtn) {
            addGroupBtn.disabled = !this.wsConnected;
        }
        if (discoverBtn) {
            discoverBtn.disabled = !this.wsConnected;
        }
        if (discoveredSelect) {
            discoveredSelect.disabled = !this.wsConnected || !Array.isArray(this.discoveredDevices) || this.discoveredDevices.length === 0;
        }

        // Disable remote controls unless STB is connected.
        document.querySelectorAll('[data-key]').forEach((btn) => {
            if (btn instanceof HTMLButtonElement) {
                btn.disabled = !this.wsConnected || !this.stbConnected;
            }
        });

        if (reconnectBtn) {
            reconnectBtn.style.display = !this.wsConnected && this.reconnectAttempts > 0 ? 'inline-flex' : 'none';
        }
    }

    updateConnectionInfo(loginInfo) {
        const info = document.getElementById('connectionInfo');
        const model = document.getElementById('deviceModel');
        const serial = document.getElementById('deviceSerial');

        if (!info || !model || !serial) {
            return;
        }

        if (loginInfo) {
            model.textContent = loginInfo.model_name || '-';
            serial.textContent = loginInfo.stb_ip_address_disp || '-';
            info.classList.remove('d-none');
        } else {
            info.classList.add('d-none');
        }
    }

    updateUI() {
        this.updateChannelList();
        this.updateCacheInfo();
        this.updateSystemStatus();
        this.updateFavorites();
    }

    updateChannelList() {
        const container = document.getElementById('channelListContainer');
        if (!container) {
            return;
        }

        if (this.channelsLoading) {
            container.innerHTML = `
                <div class="text-secondary small p-2">
                    Loading channel list…
                </div>
            `;
            return;
        }
        
        if (!this.stbConnected || this.state.channels.length === 0) {
            container.innerHTML = `
                <div class="text-secondary small p-2">
                    ${this.stbConnected ? 'No channels found' : 'Connect to STB to load channels.'}
                </div>
            `;
            return;
        }
        
        let channels = this.state.channels;
        
        // Filter by tab
        if (this.currentTab === 'favorites') {
            channels = channels.filter(ch => this.isChannelFavorite(ch));
        }
        
        // Filter by search
        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            channels = channels.filter(ch => 
                (ch.ServiceName || '').toLowerCase().includes(term) ||
                (ch.ServiceNum || '').toString().includes(term)
            );
        }
        
        container.innerHTML = channels.map(channel => this.renderChannelItem(channel)).join('');

        // Delegate actions
        container.querySelectorAll('[data-channel-index]').forEach((row) => {
            row.addEventListener('click', (e) => {
                const t = e.target;
                if (!(t instanceof HTMLElement)) {
                    return;
                }

                const idx = Number(row.getAttribute('data-channel-index') || '0');
                const channel = channels.find((c) => (c.ServiceIndex || c.ProgramIndex || 0) === idx);
                if (!channel) {
                    return;
                }

                if (t.closest('[data-action="fav"]')) {
                    e.preventDefault();
                    e.stopPropagation();
                    this.toggleFavorite(channel);
                    return;
                }
                if (t.closest('[data-action="play"]')) {
                    e.preventDefault();
                    e.stopPropagation();
                    this.playChannel(channel);
                    return;
                }

                this.playChannel(channel);
            });
        });
    }

    renderChannelItem(channel) {
        const channelIndex = channel.ServiceIndex || channel.ProgramIndex || 0;
        const channelName = channel.ServiceName || channel.ProgramName || 'Unknown';
        const channelNumber = channel.ServiceNum || channel.ProgramNum || channelIndex;
        const isFavorite = this.isChannelFavorite(channel);
        const isCurrent = this.state.currentChannel === channelIndex;
        
        return `
            <div class="list-group-item list-group-item-action bg-transparent text-white border-secondary d-flex align-items-center gap-2 py-2"
                 data-channel-index="${channelIndex}">
                <span class="badge text-bg-secondary">${channelNumber}</span>
                <div class="flex-grow-1 text-truncate">
                    <div class="fw-semibold text-truncate">${this.escapeHtml(channelName)}</div>
                    <div class="text-secondary small">Index: ${channelIndex}${isCurrent ? ' • Current' : ''}</div>
                </div>
                <button class="btn btn-sm btn-outline-warning" type="button" data-action="fav" title="${isFavorite ? 'Remove from favorites' : 'Add to favorites'}">${isFavorite ? '★' : '☆'}</button>
                <button class="btn btn-sm btn-outline-light" type="button" data-action="play">Play</button>
            </div>
        `;
    }

    escapeHtml(s) {
        return String(s)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

    isChannelFavorite(channel) {
        // Check if channel is in any favorite group
        const programId = channel.ProgramId || channel.ServiceID || channel.ServiceId;
        if (!programId) return false;
        
        return this.state.favoriteGroups.some(group =>
            Array.isArray(group.channels) && group.channels.some(fav => {
                // Support both shapes:
                // - favorites_summary: {program_id: "..."}
                // - channel-shaped: {ProgramId: "..."}
                const favPid = fav?.ProgramId ?? fav?.program_id;
                return favPid !== undefined && String(favPid) === String(programId);
            })
        );
    }

    playChannel(channelOrIndex) {
        const channelIndex = typeof channelOrIndex === 'object' 
            ? (channelOrIndex.ServiceIndex || channelOrIndex.ProgramIndex || 0)
            : channelOrIndex;
            
        this.sendMessage('channel_play', { index: channelIndex },
            () => {
                this.showNotification('Channel changed', 'success');
            },
            (error) => {
                this.showNotification(`Error changing channel: ${error}`, 'error');
            }
        );
    }

    toggleFavorite(channel) {
        const channelIndex = channel.ServiceIndex || channel.ProgramIndex || 0;
        const isFavorite = this.isChannelFavorite(channel);
        
        if (isFavorite) {
            this.sendMessage('remove_favorite', { channel_index: channelIndex },
                () => {
                    this.showNotification('Removed from favorites', 'info');
                }
            );
        } else {
            this.sendMessage('add_favorite', { channel_index: channelIndex, group_ids: [1] },
                () => {
                    this.showNotification('Added to favorites', 'success');
                }
            );
        }
    }

    sendRemoteKey(key) {
        if (!this.stbConnected) {
            this.showNotification('Please connect to STB first', 'error');
            return;
        }
        
        // Map UI keys to remote key values
        const keyMap = {
            '0': 0, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9,
            'up': 2, 'down': 3, 'left': 4, 'right': 5, 'ok': 8,
            'menu': 9, 'back': 10, 'exit': 12, 'info': 15,
            '*': 42, '#': 35
        };
        
        const keyValue = keyMap[key];
        if (keyValue !== undefined) {
            this.sendMessage('remote_key', { key_value: keyValue },
                () => {
                    console.log(`Remote key sent: ${key} (${keyValue})`);
                }
            );
        }
    }

    updateCacheInfo() {
        if (!this.state.cacheInfo) return;
        
        const text = document.getElementById('cacheText');
        if (!text) {
            return;
        }
        
        if (this.state.cacheInfo.cache_valid) {
            const ageMinutes = Math.round(this.state.cacheInfo.cache_age_minutes);
            text.textContent = `Cache valid (${ageMinutes}m ago)`;
            text.classList.remove('text-secondary');
            text.classList.add('text-success');
        } else {
            text.textContent = 'Cache invalid';
            text.classList.remove('text-success');
            text.classList.add('text-secondary');
        }
    }

    updateSystemStatus() {
        const channelCountEl = document.getElementById('channelCount');
        const currentChannelEl = document.getElementById('currentChannel');
        const cacheStatusEl = document.getElementById('cacheStatus');

        if (channelCountEl) channelCountEl.textContent = String(this.state.channels.length);
        if (currentChannelEl) currentChannelEl.textContent = String(this.state.currentChannel || '-');
        
        if (this.state.cacheInfo) {
            if (cacheStatusEl) {
                cacheStatusEl.textContent = this.state.cacheInfo.cache_valid ? 'Valid' : 'Invalid';
            }
        }
    }

    updateFavorites() {
        const container = document.getElementById('favoritesContainer');
        if (!container) {
            return;
        }
        
        if (this.state.favoriteGroups.length === 0) {
            container.innerHTML = `
                <div class="text-secondary small">No favorite groups</div>
            `;
            return;
        }

        container.innerHTML = this.state.favoriteGroups.map(group => this.renderFavoriteGroup(group)).join('');
    }

    renderFavoriteGroup(group) {
        return `
            <div class="border border-secondary rounded-2 p-2 bg-black bg-opacity-25">
                <div class="d-flex align-items-center justify-content-between">
                    <div class="fw-semibold">${this.escapeHtml(group.group_name)}</div>
                    <span class="badge text-bg-secondary">${group.channel_count} ch</span>
                </div>
                <div class="d-flex gap-2 mt-2 flex-wrap">
                    <button class="btn btn-sm btn-outline-light" onclick="app.viewGroupChannels(${group.group_id})">View</button>
                    <button class="btn btn-sm btn-outline-light" onclick="app.renameGroup(${group.group_id})">Rename</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="app.deleteGroup(${group.group_id})">Delete</button>
                </div>
            </div>
        `;
    }

    updateFavoritesCount() {
        const totalFavorites = this.state.favoriteGroups.reduce(
            (sum, group) => sum + (group.channel_count || 0), 0
        );
        const el = document.getElementById('favoritesCount');
        if (el) el.textContent = String(totalFavorites);
    }

    viewGroupChannels(groupId) {
        this.sendMessage('get_favorites', { group_id: groupId },
            (data) => {
                console.log('Group channels:', data.favorites);
                // Switch to favorites tab and filter by group
                this.switchTab('favorites');
            }
        );
    }

    showAddGroupDialog() {
        const name = prompt('Enter new group name:');
        if (name && name.trim()) {
            this.sendMessage('create_favorite_group', { group_name: name.trim() },
                (data) => {
                    this.showNotification('New group created', 'success');
                }
            );
        }
    }

    renameGroup(groupId) {
        const newName = prompt('Enter new group name:');
        if (newName && newName.trim()) {
            this.sendMessage('rename_favorite_group', { 
                group_id: groupId, 
                new_name: newName.trim() 
            },
                (data) => {
                    this.showNotification('Group renamed', 'success');
                }
            );
        }
    }

    deleteGroup(groupId) {
        if (confirm('Are you sure you want to delete this group?')) {
            this.sendMessage('delete_favorite_group', { group_id: groupId },
                (data) => {
                    this.showNotification('Group deleted', 'info');
                }
            );
        }
    }

    showNotification(message, type = 'info') {
        const container = document.getElementById('notificationContainer');
        if (!container) {
            return;
        }

        const colorMap = {
            success: 'text-bg-success',
            error: 'text-bg-danger',
            warning: 'text-bg-warning',
            info: 'text-bg-primary',
        };
        const cls = colorMap[type] || 'text-bg-primary';

        const el = document.createElement('div');
        el.className = `toast align-items-center ${cls} border-0`;
        el.setAttribute('role', 'alert');
        el.setAttribute('aria-live', 'assertive');
        el.setAttribute('aria-atomic', 'true');
        el.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">${this.escapeHtml(message)}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;
        container.appendChild(el);

        try {
            const t = bootstrap.Toast.getOrCreateInstance(el, { delay: 3500 });
            t.show();
            el.addEventListener('hidden.bs.toast', () => {
                el.remove();
            });
        } catch (e) {
            console.log('NOTIFY:', message);
            setTimeout(() => el.remove(), 3500);
        }
    }
}

// Initialize the application
const app = new STBApp();

// Make app globally available for inline event handlers
window.app = app;
