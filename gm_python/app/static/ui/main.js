const React = window.React;
const ReactDOM = window.ReactDOM;
const ReactDOMClient = window.ReactDOMClient;
if (!React || !ReactDOM) {
  throw new Error(
    "React vendor files are not loaded. Ensure /static/ui/vendor/react.production.min.js and react-dom.production.min.js are present."
  );
}

const { useEffect, useMemo, useRef, useState } = React;

function now() {
  return new Date().toLocaleTimeString();
}

function safeJsonParse(s) {
  try {
    return JSON.parse(s);
  } catch {
    return null;
  }
}

function randomId() {
  return Math.random().toString(16).slice(2) + Math.random().toString(16).slice(2);
}

function App() {
  const [wsStatus, setWsStatus] = useState("disconnected");
  const [stbConnected, setStbConnected] = useState(false);
  const [loginInfo, setLoginInfo] = useState(null);
  const [currentChannelIndex, setCurrentChannelIndex] = useState(null);

  const [ip, setIp] = useState(localStorage.getItem("stb_ip") || "192.168.1.2");
  const [port, setPort] = useState(Number(localStorage.getItem("stb_port") || 20000));

  const [channelsCount, setChannelsCount] = useState(0);
  const [channelsExpected, setChannelsExpected] = useState(null);
  const [channelsLoading, setChannelsLoading] = useState(false);

  const channelsMapRef = useRef(new Map());
  const [channelsVersion, setChannelsVersion] = useState(0);
  const [search, setSearch] = useState("");

  const [epgCount, setEpgCount] = useState(0);
  const [favCount, setFavCount] = useState(0);
  const [timerCount, setTimerCount] = useState(0);
  const [stbInfo, setStbInfo] = useState(null);

  const [tab, setTab] = useState("dashboard");
  const [logText, setLogText] = useState("");

  // Keyboard state
  const [keyboardText, setKeyboardText] = useState("");
  const [liveMode, setLiveMode] = useState(false);
  const [forceMode, setForceMode] = useState(false);
  const [kbdEvent, setKbdEvent] = useState("-");

  const wsRef = useRef(null);
  const pendingRef = useRef(new Map());
  const wsReconnectTimeoutRef = useRef(null);
  const stbReconnectTimeoutRef = useRef(null);
  const wsReconnectAttemptsRef = useRef(0);
  const stbReconnectAttemptsRef = useRef(0);
  const lastStbTargetRef = useRef({ ip, port });

  const log = (line) => {
    setLogText((prev) => {
      const next = `${prev}[${now()}] ${line}\n`;
      return next.length > 12000 ? next.slice(next.length - 12000) : next;
    });
  };

  const send = (type, payload = {}) => {
    const ws = wsRef.current;
    if (!ws || ws.readyState !== 1) {
      return Promise.reject(new Error("WebSocket not connected"));
    }

    const id = randomId();
    const msg = { type, id, payload };

    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        pendingRef.current.delete(id);
        reject(new Error(`timeout waiting for response: ${type}`));
      }, 20000);

      pendingRef.current.set(id, {
        resolve: (data) => {
          clearTimeout(timeout);
          resolve(data);
        },
        reject: (err) => {
          clearTimeout(timeout);
          reject(err);
        },
      });

      ws.send(JSON.stringify(msg));
    });
  };

  const connectWs = () => {
    if (wsRef.current && wsRef.current.readyState === 1) return;

    setWsStatus("connecting");
    const ws = new WebSocket(`ws://${location.host}/ws`);
    wsRef.current = ws;

    ws.onopen = () => {
      setWsStatus("connected");
      wsReconnectAttemptsRef.current = 0;
      log("WS connected");
    };

    ws.onclose = () => {
      setWsStatus("disconnected");
      setStbConnected(false);
      setLoginInfo(null);
      log("WS disconnected");
      
      // Clear any pending STB reconnect when WS closes
      if (stbReconnectTimeoutRef.current) {
        clearTimeout(stbReconnectTimeoutRef.current);
        stbReconnectTimeoutRef.current = null;
      }
      
      // Auto-reconnect WebSocket with exponential backoff
      const delays = [1000, 2000, 5000, 10000, 15000];
      const delay = delays[Math.min(wsReconnectAttemptsRef.current, delays.length - 1)];
      wsReconnectAttemptsRef.current++;
      
      log(`WS reconnect attempt ${wsReconnectAttemptsRef.current} in ${delay}ms`);
      wsReconnectTimeoutRef.current = setTimeout(() => {
        wsReconnectTimeoutRef.current = null;
        connectWs();
      }, delay);
    };

    ws.onerror = () => {
      log("WS error");
    };

    ws.onmessage = (evt) => {
      if (typeof evt.data !== "string") return;
      const msg = safeJsonParse(evt.data);
      if (!msg) return;

      if (msg.type === "response") {
        const id = msg.id;
        const pending = pendingRef.current.get(id);
        if (pending) {
          pendingRef.current.delete(id);
          if (msg.ok) pending.resolve(msg.data);
          else pending.reject(new Error(msg.error || "unknown error"));
        }
        return;
      }

      if (msg.type === "snapshot") {
        setStbConnected(!!msg.connected);
        setLoginInfo(msg.login_info || null);

        const state = msg.state || null;
        if (state) {
          setCurrentChannelIndex(
            state.current_channel_index === undefined ? null : Number(state.current_channel_index)
          );

          const info = state.stb_info || {};
          if (info && info.ChannelNum !== undefined) {
            const exp = Number(info.ChannelNum);
            if (!Number.isNaN(exp)) setChannelsExpected(exp);
          }
          setStbInfo(info);

          if (Array.isArray(state.channels)) {
            const m = new Map();
            let fav = 0;
            for (const ch of state.channels) {
              const idx = ch?.ServiceIndex ?? ch?.ProgramIndex;
              if (idx === undefined || idx === null) continue;
              m.set(Number(idx), ch);
              if (ch.FavMark && ch.FavMark > 0) fav++;
            }
            channelsMapRef.current = m;
            setChannelsCount(m.size);
            setFavCount(fav);
            setChannelsVersion((v) => v + 1);
          }

          setEpgCount(Array.isArray(state.epg_events) ? state.epg_events.length : 0);
          setTimerCount(Array.isArray(state.timers) ? state.timers.length : 0);
        }
        return;
      }

      if (msg.event) {
        const ev = msg.event;
        const data = msg.data;

        if (ev === "disconnected") {
          setStbConnected(false);
          setLoginInfo(null);
          setStbInfo(null);
          setEpgCount(0);
          setFavCount(0);
          setTimerCount(0);
          log("STB disconnected");
          
          // Auto-reconnect STB with exponential backoff
          if (lastStbTargetRef.current.ip) {
            const delays = [2000, 5000, 10000, 20000, 30000];
            const delay = delays[Math.min(stbReconnectAttemptsRef.current, delays.length - 1)];
            stbReconnectAttemptsRef.current++;
            
            log(`STB auto-reconnect attempt ${stbReconnectAttemptsRef.current} in ${delay}ms`);
            stbReconnectTimeoutRef.current = setTimeout(async () => {
              stbReconnectTimeoutRef.current = null;
              try {
                log(`Auto-connecting to STB: ${lastStbTargetRef.current.ip}:${lastStbTargetRef.current.port}`);
                await send("connect", lastStbTargetRef.current);
                setStbConnected(true);
                stbReconnectAttemptsRef.current = 0;
                log("STB auto-reconnect successful");
              } catch (e) {
                log(`STB auto-reconnect failed: ${e.message}`);
              }
            }, delay);
          }
        }

        if (ev === "stb_info") {
          if (data && data.ChannelNum !== undefined) {
            const exp = Number(data.ChannelNum);
            if (!Number.isNaN(exp)) setChannelsExpected(exp);
          }
          setStbInfo(data);
        }

        if (ev === "current_channel") {
          if (data && typeof data === "object" && data.index !== undefined) {
            setCurrentChannelIndex(Number(data.index));
          } else {
            setCurrentChannelIndex(Number(data));
          }
        }

        if (ev === "channel_list_reset") {
          channelsMapRef.current = new Map();
          setChannelsCount(0);
          setFavCount(0);
          setChannelsVersion((v) => v + 1);
          setChannelsLoading(true);
        }

        if (ev === "channel_upsert") {
          const ch = data;
          const idx = ch?.ServiceIndex ?? ch?.ProgramIndex;
          if (idx !== undefined && idx !== null) {
            channelsMapRef.current.set(Number(idx), ch);
            setChannelsCount(channelsMapRef.current.size);
            setChannelsVersion((v) => v + 1);
            
            // Update favorite count
            const favCount = Array.from(channelsMapRef.current.values()).filter(c => c.FavMark && c.FavMark > 0).length;
            setFavCount(favCount);
          }
        }

        if (ev === "channel_list_progress") {
          if (data && typeof data.count === "number") setChannelsCount(data.count);
          setChannelsLoading(true);
        }

        if (ev === "channel_list_complete") {
          setChannelsLoading(false);
        }

        if (ev === "epg_data") {
          setEpgCount(Array.isArray(data) ? data.length : 0);
        }

        if (ev === "timer_list") {
          setTimerCount(Array.isArray(data) ? data.length : 0);
        }
      }
    };
  };

  useEffect(() => {
    connectWs();
    return () => {
      const ws = wsRef.current;
      if (ws) ws.close();
      
      // Clear any pending reconnect timeouts
      if (wsReconnectTimeoutRef.current) {
        clearTimeout(wsReconnectTimeoutRef.current);
        wsReconnectTimeoutRef.current = null;
      }
      if (stbReconnectTimeoutRef.current) {
        clearTimeout(stbReconnectTimeoutRef.current);
        stbReconnectTimeoutRef.current = null;
      }
    };
  }, []);

  const connectedPill = useMemo(() => {
    const wsOk = wsStatus === "connected";
    const dotClass = wsOk ? (stbConnected ? "green" : "yellow") : "red";
    const text = wsOk ? (stbConnected ? "Connected" : "WS only") : "Offline";
    return { dotClass, text };
  }, [wsStatus, stbConnected]);

  const channelsList = useMemo(() => {
    // trigger memo re-eval
    void channelsVersion;

    const items = Array.from(channelsMapRef.current.entries())
      .map(([idx, ch]) => ({ idx, ch }))
      .sort((a, b) => a.idx - b.idx);

    const q = search.trim().toLowerCase();
    if (!q) return items;

    return items.filter(({ idx, ch }) => {
      const name = String(ch?.ServiceName || "").toLowerCase();
      return String(idx).includes(q) || name.includes(q);
    });
  }, [channelsVersion, search]);

  const doConnectStb = async () => {
    try {
      localStorage.setItem("stb_ip", ip);
      localStorage.setItem("stb_port", String(port));
      lastStbTargetRef.current = { ip, port };
      stbReconnectAttemptsRef.current = 0;
      
      // Clear any pending auto-reconnect
      if (stbReconnectTimeoutRef.current) {
        clearTimeout(stbReconnectTimeoutRef.current);
        stbReconnectTimeoutRef.current = null;
      }
      
      log(`connect STB: ${ip}:${port}`);
      await send("connect", { ip, port });
      setStbConnected(true);
    } catch (e) {
      log(`connect error: ${e.message}`);
    }
  };

  const doDisconnectStb = async () => {
    try {
      // Clear any pending auto-reconnect
      if (stbReconnectTimeoutRef.current) {
        clearTimeout(stbReconnectTimeoutRef.current);
        stbReconnectTimeoutRef.current = null;
      }
      stbReconnectAttemptsRef.current = 0;
      
      log("disconnect STB");
      await send("disconnect", {});
      setStbConnected(false);
      setLoginInfo(null);
      setStbInfo(null);
      setEpgCount(0);
      setFavCount(0);
      setTimerCount(0);
    } catch (e) {
      log(`disconnect error: ${e.message}`);
    }
  };

  const doRefreshChannels = async (force) => {
    try {
      setChannelsLoading(true);
      log(`refresh_channels force=${force}`);
      const out = await send("refresh_channels", { force, timeout: 60.0 });
      log(`refresh_channels done count=${out?.channel_count}`);
    } catch (e) {
      setChannelsLoading(false);
      log(`refresh_channels error: ${e.message}`);
    }
  };

  const doPlay = async (index) => {
    try {
      await send("channel_play", { index });
    } catch (e) {
      log(`play error: ${e.message}`);
    }
  };

  const doKey = async (key_value) => {
    try {
      await send("remote_key", { key_value });
    } catch (e) {
      log(`key error: ${e.message}`);
    }
  };

  const doKeyboardSend = async () => {
    try {
      log(`keyboard send: "${keyboardText}"`);
      await send("keyboard_send", { text: keyboardText, force: forceMode });
    } catch (e) {
      log(`keyboard send error: ${e.message}`);
    }
  };

  const doKeyboardKey = async (key) => {
    try {
      await send("keyboard_key", { key, force: forceMode });
    } catch (e) {
      log(`keyboard key error: ${e.message}`);
    }
  };

  const doKeyboardCode = async (code) => {
    try {
      await send("keyboard_code", { code, force: forceMode });
    } catch (e) {
      log(`keyboard code error: ${e.message}`);
    }
  };

  const expected = channelsExpected || null;
  const progressText = expected ? `${channelsCount} / ${expected}` : `${channelsCount}`;

  const currentChannel = useMemo(() => {
    if (currentChannelIndex === null) return null;
    return channelsMapRef.current.get(currentChannelIndex);
  }, [currentChannelIndex, channelsVersion]);

  const h = React.createElement;

  return h(
    "div",
    { className: "container" },
    h(
      "div",
      { className: "topbar" },
      h(
        "div",
        { className: "brand" },
        h("b", null, "GMScreen"),
        h("span", null, "Realtime • WebSocket only UI")
      ),
      h(
        "div",
        { className: "pill" },
        h("span", { className: `dot ${connectedPill.dotClass}` }),
        h("span", null, connectedPill.text)
      )
    ),
    h(
      "div",
      { className: "row" },
      h(
        "div",
        { className: "card" },
        h(
          "div",
          { className: "cardHeader" },
          h("h3", null, "Connection"),
          h(
            "div",
            { className: "tabs" },
            h(
              "button",
              {
                className: `tab ${tab === "dashboard" ? "active" : ""}`,
                onClick: () => setTab("dashboard"),
              },
              "Dashboard"
            ),
            h(
              "button",
              {
                className: `tab ${tab === "channels" ? "active" : ""}`,
                onClick: () => setTab("channels"),
              },
              "Channels"
            ),
            h(
              "button",
              {
                className: `tab ${tab === "remote" ? "active" : ""}`,
                onClick: () => setTab("remote"),
              },
              "Remote"
            ),
            h(
              "button",
              {
                className: `tab ${tab === "keyboard" ? "active" : ""}`,
                onClick: () => setTab("keyboard"),
              },
              "Keyboard"
            ),
            h(
              "button",
              {
                className: `tab ${tab === "log" ? "active" : ""}`,
                onClick: () => setTab("log"),
              },
              "Log"
            )
          )
        ),
        h(
          "div",
          { className: "cardBody" },
          h(
            "div",
            { className: "inputs" },
            h("input", {
              value: ip,
              onChange: (e) => setIp(e.target.value),
              placeholder: "STB IP",
            }),
            h("input", {
              value: port,
              onChange: (e) => setPort(Number(e.target.value || 0)),
              placeholder: "Port",
              type: "number",
            }),
            h(
              "button",
              {
                className: "btn",
                disabled: wsStatus !== "connected" || stbConnected,
                onClick: doConnectStb,
              },
              "Connect STB"
            ),
            h(
              "button",
              { className: "btn danger", disabled: !stbConnected, onClick: doDisconnectStb },
              "Disconnect"
            ),
            h("button", { className: "btn secondary", onClick: connectWs }, "Reconnect WS")
          ),
          h(
            "div",
            { style: { marginTop: 10 }, className: "muted" },
            h("div", null, h("b", null, "WS:"), " ", wsStatus),
            h(
              "div",
              null,
              h("b", null, "STB:"),
              " ",
              stbConnected ? "connected" : "not connected"
            ),
            h("div", null, h("b", null, "Model:"), " ", loginInfo?.model_name || "-"),
            h(
              "div",
              null,
              h("b", null, "Send type:"),
              " ",
              loginInfo?.send_data_type ?? "-"
            )
          )
        )
      ),
      h(
        "div",
        { className: "card" },
        h(
          "div",
          { className: "cardHeader" },
          h("h3", null, "Status"),
          h(
            "span",
            { className: "muted" },
            `Channels: ${progressText}${channelsLoading ? " (loading...)" : ""}`
          )
        ),
        h(
          "div",
          { className: "cardBody" },
          h(
            "div",
            { className: "inputs" },
            h(
              "button",
              { className: "btn", disabled: !stbConnected, onClick: () => doRefreshChannels(false) },
              "Refresh Channels"
            ),
            h(
              "button",
              {
                className: "btn secondary",
                disabled: !stbConnected,
                onClick: () => doRefreshChannels(true),
              },
              "Force Refresh"
            ),
            h("input", {
              value: search,
              onChange: (e) => setSearch(e.target.value),
              placeholder: "Search name/index...",
            })
          ),
          h(
            "div",
            { style: { marginTop: 10 }, className: "muted" },
            `Current channel index: ${currentChannelIndex ?? "-"}`
          )
        )
      )
    ),
    tab === "dashboard"
      ? h(
          "div",
          { className: "card", style: { marginTop: 14 } },
          h(
            "div",
            { className: "cardHeader" },
            h("h3", null, "Live Monitor"),
            h(
              "span",
              { className: "muted" },
              stbConnected ? "Connected" : "Disconnected"
            )
          ),
          h(
            "div",
            { className: "cardBody" },
            h(
              "div",
              { className: "inputs" },
              h(
                "div",
                { style: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" } },
                h(
                  "div",
                  { style: { padding: "10px", border: "1px solid var(--border)", borderRadius: "8px" } },
                  h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, "Current Channel"),
                  h("div", { style: { fontSize: "18px", fontWeight: "bold" } }, currentChannel?.ServiceName || "-"),
                  h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, `Index: ${currentChannelIndex ?? "-"}`)
                ),
                h(
                  "div",
                  { style: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px" } },
                  h(
                    "div",
                    { style: { padding: "10px", border: "1px solid var(--border)", borderRadius: "8px", textAlign: "center" } },
                    h("div", { style: { fontSize: "24px", fontWeight: "bold", color: "var(--accent)" } }, channelsCount),
                    h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, "Channels")
                  ),
                  h(
                    "div",
                    { style: { padding: "10px", border: "1px solid var(--border)", borderRadius: "8px", textAlign: "center" } },
                    h("div", { style: { fontSize: "24px", fontWeight: "bold", color: "var(--green)" } }, epgCount),
                    h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, "EPG")
                  ),
                  h(
                    "div",
                    { style: { padding: "10px", border: "1px solid var(--border)", borderRadius: "8px", textAlign: "center" } },
                    h("div", { style: { fontSize: "24px", fontWeight: "bold", color: "var(--yellow)" } }, favCount),
                    h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, "Favorites")
                  ),
                  h(
                    "div",
                    { style: { padding: "10px", border: "1px solid var(--border)", borderRadius: "8px", textAlign: "center" } },
                    h("div", { style: { fontSize: "24px", fontWeight: "bold", color: "#a855f7" } }, timerCount),
                    h("div", { style: { fontSize: "12px", color: "var(--muted)" } }, "Timers")
                  )
                )
              )
            )
          )
        )
      : null,
    tab === "channels"
      ? h(
          "div",
          { className: "card", style: { marginTop: 14 } },
          h(
            "div",
            { className: "cardHeader" },
            h("h3", null, "Channels"),
            h("span", { className: "muted" }, `${channelsList.length} shown`)
          ),
          h(
            "div",
            { className: "cardBody" },
            h(
              "div",
              { className: "list" },
              h(
                "div",
                { className: "scroll" },
                channelsList.map(({ idx, ch }) =>
                  h(
                    "div",
                    { key: idx, className: "listRow" },
                    h("div", { className: "idx" }, `#${idx}`),
                    h("div", { className: "name" }, ch?.ServiceName || "(no name)"),
                    h(
                      "div",
                      { className: "actions" },
                      h(
                        "button",
                        {
                          className: "btn secondary",
                          disabled: !stbConnected,
                          onClick: () => doPlay(idx),
                        },
                        "Play"
                      )
                    )
                  )
                )
              )
            )
          )
        )
      : null,
    tab === "remote"
      ? h(
          "div",
          { className: "card", style: { marginTop: 14 } },
          h(
            "div",
            { className: "cardHeader" },
            h("h3", null, "Remote"),
            h("span", { className: "muted" }, "WebSocket commands")
          ),
          h(
            "div",
            { className: "cardBody" },
            h(
              "div",
              { className: "inputs" },
              h(
                "button",
                { className: "btn", disabled: !stbConnected, onClick: () => doKey(0x05) },
                "POWER"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x0b) },
                "MENU"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x29) },
                "EXIT"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x02) },
                "UP"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x03) },
                "DOWN"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x04) },
                "LEFT"
              ),
              h(
                "button",
                { className: "btn secondary", disabled: !stbConnected, onClick: () => doKey(0x01) },
                "RIGHT"
              ),
              h(
                "button",
                { className: "btn", disabled: !stbConnected, onClick: () => doKey(0x1c) },
                "OK"
              )
            ),
            h(
              "div",
              { style: { marginTop: 10 }, className: "muted" },
              "If your platform keycodes differ, tell me your correct keymap and I'll align it."
            )
          )
        )
      : null,
    tab === "keyboard"
      ? h(
          "div",
          { className: "card", style: { marginTop: 14 } },
          h(
            "div",
            { className: "cardHeader" },
            h("h3", null, "Keyboard"),
            h(
              "span",
              { className: "muted" },
              "Send text to STB"
            )
          ),
          h(
            "div",
            { className: "cardBody" },
            h(
              "div",
              { className: "inputs" },
              h("textarea", {
                value: keyboardText,
                onChange: (e) => setKeyboardText(e.target.value),
                placeholder: "Type here...",
                style: { width: "100%", minHeight: "100px", padding: "8px", borderRadius: "8px", border: "1px solid var(--border)", background: "var(--panel2)", color: "var(--text)", fontFamily: "monospace" }
              }),
              h(
                "div",
                { style: { marginTop: "10px", display: "flex", flexWrap: "wrap", gap: "8px" } },
                h("button", { className: "btn", disabled: !stbConnected, onClick: doKeyboardSend }, "Send Text"),
                h("button", { className: "btn secondary", disabled: !stbConnected, onClick: () => doKeyboardKey("enter") }, "Enter"),
                h("button", { className: "btn secondary", disabled: !stbConnected, onClick: () => doKeyboardKey("backspace") }, "Backspace"),
                h("button", { className: "btn secondary", disabled: !stbConnected, onClick: () => doKeyboardKey("space") }, "Space"),
                h("button", { className: "btn secondary", disabled: !stbConnected, onClick: () => doKeyboardKey("tab") }, "Tab"),
                h("button", { className: "btn secondary", disabled: !stbConnected, onClick: () => doKeyboardKey("dismiss") }, "Dismiss"),
                h("button", { className: "btn secondary", onClick: () => setKeyboardText("") }, "Clear")
              ),
              h(
                "div",
                { style: { marginTop: "10px", display: "flex", gap: "20px" } },
                h(
                  "label",
                  { style: { display: "flex", alignItems: "center", gap: "5px", fontSize: "12px" } },
                  h("input", { type: "checkbox", checked: liveMode, onChange: (e) => setLiveMode(e.target.checked) }),
                  "Live Mode"
                ),
                h(
                  "label",
                  { style: { display: "flex", alignItems: "center", gap: "5px", fontSize: "12px" } },
                  h("input", { type: "checkbox", checked: forceMode, onChange: (e) => setForceMode(e.target.checked) }),
                  "Force Mode"
                )
              )
            ),
            h(
              "div",
              { style: { marginTop: "10px" } },
              h("div", { style: { fontSize: "12px", color: "var(--muted)", marginBottom: "5px" } }, "Keyboard Events"),
              h("div", { 
                style: { 
                  fontSize: "11px", 
                  color: "var(--text)", 
                  background: "var(--panel2)", 
                  padding: "8px", 
                  borderRadius: "8px", 
                  fontFamily: "monospace",
                  minHeight: "60px",
                  whiteSpace: "pre-wrap",
                  overflow: "auto"
                } 
              }, kbdEvent)
            )
          )
        )
      : null,
    tab === "log"
      ? h(
          "div",
          { className: "card", style: { marginTop: 14 } },
          h(
            "div",
            { className: "cardHeader" },
            h("h3", null, "Log"),
            h(
              "button",
              { className: "btn secondary", onClick: () => setLogText("") },
              "Clear"
            )
          ),
          h("div", { className: "cardBody" }, h("div", { className: "log" }, logText))
        )
      : null,
    h(
      "div",
      { style: { marginTop: 16 }, className: "muted" },
      "Note: UI uses only WebSocket. Backend still has legacy HTML pages, but this is the new UI."
    )
  );
}

const _rootEl = document.getElementById("root");
if (ReactDOMClient && typeof ReactDOMClient.createRoot === "function") {
  ReactDOMClient.createRoot(_rootEl).render(React.createElement(App));
} else if (typeof ReactDOM.createRoot === "function") {
  ReactDOM.createRoot(_rootEl).render(React.createElement(App));
} else {
  ReactDOM.render(React.createElement(App), _rootEl);
}
