from __future__ import annotations

import asyncio
import json
import logging
from typing import Dict, Any, Optional, List

from app.stb.websocket_client import STBWebSocketBridge, ReceivedMessage
from app.stb.message_processor import MessageProcessor, STBState

logger = logging.getLogger(__name__)


class STBWebSocketManager:
    """Manages STB connection via WebSocket with channel list caching"""
    
    def __init__(self):
        self.bridge: Optional[STBWebSocketBridge] = None
        self.processor = MessageProcessor()
        self._connected = False
        
    async def connect(self, websocket, stb_host: str = "192.168.1.100", stb_port: int = 5450) -> bool:
        """Connect to STB via WebSocket bridge"""
        try:
            self.bridge = STBWebSocketBridge(websocket)
            self.bridge.on_message = self._handle_message
            self.bridge.on_disconnect = self._handle_disconnect
            
            # Connect to STB
            success = await self.bridge.connect_to_stb(stb_host, stb_port)
            if not success:
                return False
            
            self._connected = True
            logger.info("STB WebSocket bridge connected")
            return True
            
        except Exception as e:
            logger.error(f"STB WebSocket connection failed: {e}")
            return False
            
    async def disconnect(self) -> None:
        """Disconnect from STB"""
        if self.bridge:
            await self.bridge.disconnect()
            self.bridge = None
        self._connected = False
        logger.info("STB WebSocket disconnected")
        
    async def _handle_message(self, msg: ReceivedMessage) -> None:
        """Handle received message from STB"""
        self.processor.process_message(msg)
        
    def _handle_disconnect(self) -> None:
        """Handle disconnection"""
        self._connected = False
        logger.info("STB disconnected")
        
    async def request_stb_info(self) -> None:
        """Request STB information"""
        if self.bridge:
            await self.bridge.send_command(0x26)
            
    async def request_channel_list(self, timeout: float = 60.0, force_refresh: bool = False) -> int:
        """Request channel list in batches like APK"""
        if not self.bridge:
            raise ConnectionError("STB not connected")
            
        # Check cache first
        if not force_refresh and self.processor.is_channels_cache_valid():
            logger.info(f"Using cached channel list: {len(self.processor.state.channels)} channels")
            return len(self.processor.state.channels)
            
        # Clear cache if forcing refresh
        if force_refresh:
            self.processor.clear_channels_cache()
            
        # Start waiting for completion
        complete_event = self.processor.start_channel_list_wait()
        
        # Get total channels from STB info
        total_channels = 3740  # Default
        if self.processor.state.stb_info and "ChannelNum" in self.processor.state.stb_info:
            total_channels = int(self.processor.state.stb_info["ChannelNum"])
            
        batch_size = 100  # From APK
        batches_requested = 0
        
        # Request channels in batches
        for from_idx in range(0, total_channels, batch_size):
            to_idx = min(from_idx + batch_size - 1, total_channels - 1)
            
            logger.info(f"Requesting channel batch: {from_idx} to {to_idx}")
            await self.bridge.send_command(0x00, [
                {"ProgramIndex": str(from_idx)},
                {"ProgramIndex": str(to_idx)}
            ])
            batches_requested += 1
            
            # Small delay between batches
            await asyncio.sleep(0.1)
            
        logger.info(f"Sent {batches_requested} channel list requests")
        
        # Wait for completion
        try:
            await asyncio.wait_for(complete_event.wait(), timeout=timeout)
            logger.info(f"Channel list loading completed: {len(self.processor.state.channels)} channels")
            return len(self.processor.state.channels)
        except asyncio.TimeoutError:
            logger.warning(f"Channel list loading timed out after {timeout}s, got {len(self.processor.state.channels)} channels")
            return len(self.processor.state.channels)
            
    async def send_remote_key(self, key: int) -> None:
        """Send remote key command"""
        if self.bridge:
            await self.bridge.send_command(0x410, [str(key)])
            
    def get_state(self) -> STBState:
        """Get current STB state"""
        return self.processor.state
        
    def is_connected(self) -> bool:
        """Check if connected to STB"""
        return self._connected and self.bridge is not None
