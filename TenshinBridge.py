import os
import sys
import json
import time
import socket
import asyncio
import threading
import subprocess
import requests
from pathlib import Path
from websockets.server import serve
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from zeroconf import ServiceInfo, Zeroconf
import pystray
from PIL import Image, ImageDraw

# --- CONFIGURATION ---
WS_PORT = 8080
SAINAN_RELEASE_URL = "https://github.com/Sainan/warframe-api-helper/releases/download/1.1.1/warframe-api-helper.exe"
HELPER_EXE = "warframe-api-helper.exe"
INVENTORY_FILE = "inventory.json"

class TenshinBridge:
    def __init__(self):
        self.clients = set()
        self.loop = None
        self.stop_event = threading.Event()
        self.inventory_path = Path(INVENTORY_FILE)

    def get_ip(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            s.connect(('8.8.8.8', 1))
            ip = s.getsockname()[0]
        except Exception:
            ip = '127.0.0.1'
        finally:
            s.close()
        return ip

    async def handler(self, websocket):
        self.clients.add(websocket)
        print(f"📱 Connection established from {websocket.remote_address}")

        # Immediate 1999 Protocol Handshake
        await websocket.send(json.dumps({"type": "system_status", "status": "HACKED_1999"}))

        try:
            if self.inventory_path.exists():
                data = self.inventory_path.read_text(encoding='utf-8')
                await websocket.send(json.dumps({
                    "type": "inventory",
                    "payload": json.loads(data),
                    "timestamp": time.time()
                }))

            async for message in websocket:
                pass
        except Exception as e:
            print(f"Connection error: {e}")
        finally:
            self.clients.remove(websocket)

    async def broadcast_inventory(self):
        if not self.clients or not self.inventory_path.exists():
            return
        try:
            data = json.loads(self.inventory_path.read_text(encoding='utf-8'))
            payload = json.dumps({
                "type": "inventory_update",
                "payload": data,
                "timestamp": time.time()
            })
            for client in self.clients:
                try:
                    await client.send(payload)
                except:
                    pass
        except Exception as e:
            print(f"Broadcast error: {e}")

    def start_ws_server(self):
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

        async def main_ws():
            async with serve(self.handler, "0.0.0.0", WS_PORT):
                await asyncio.Future()

        self.loop.run_until_complete(main_ws())

    def download_sainan_helper(self):
        if not os.path.exists(HELPER_EXE):
            print("💾 Downloading Sainan's Warframe API Helper...")
            try:
                r = requests.get(SAINAN_RELEASE_URL, allow_redirects=True, timeout=30)
                open(HELPER_EXE, 'wb').write(r.content)
                print("✅ Download complete.")
            except Exception as e:
                print(f"❌ Download failed: {e}")

    def run_sainan_helper(self):
        if os.path.exists(HELPER_EXE):
            print("🚀 Launching API Helper...")
            subprocess.Popen([HELPER_EXE], creationflags=subprocess.CREATE_NEW_CONSOLE)

    def on_inventory_changed(self):
        if self.loop:
            asyncio.run_coroutine_threadsafe(self.broadcast_inventory(), self.loop)

class InventoryHandler(FileSystemEventHandler):
    def __init__(self, bridge):
        self.bridge = bridge
    def on_modified(self, event):
        if not event.is_directory and event.src_path.endswith(INVENTORY_FILE):
            self.bridge.on_inventory_changed()

def run_tray(bridge):
    img = Image.new('RGB', (64, 64), (5, 5, 5))
    draw = ImageDraw.Draw(img)
    draw.text((10, 10), "1999", fill=(0, 255, 65))

    def quit_app(icon):
        icon.stop()
        os._exit(0)

    icon = pystray.Icon("TenshinBridge", img, "TENSHIN BRIDGE: 1999", menu=pystray.Menu(
        pystray.MenuItem(f"IP: {bridge.get_ip()}", lambda: None, enabled=False),
        pystray.MenuItem("Force Sync", bridge.run_sainan_helper),
        pystray.MenuItem("Exit", quit_app)
    ))
    icon.run()

if __name__ == "__main__":
    print("═" * 55)
    print("  HÖLLVANIA SYSTEM - PROTOCOL 1999 ACTIVE")
    print("  TENSHIN BRIDGE v1.0.0")
    print("═" * 55)

    bridge = TenshinBridge()
    bridge.download_sainan_helper()
    bridge.run_sainan_helper()

    zeroconf = Zeroconf()
    info = ServiceInfo("_tenshin._tcp.local.", "TenshinBridge._tenshin._tcp.local.",
                       addresses=[socket.inet_aton(bridge.get_ip())], port=WS_PORT)
    zeroconf.register_service(info)

    observer = Observer()
    observer.schedule(InventoryHandler(bridge), path='.', recursive=False)
    observer.start()

    threading.Thread(target=bridge.start_ws_server, daemon=True).start()

    print(f"📡 Waiting for Tenno connection on {bridge.get_ip()}:{WS_PORT}...")
    run_tray(bridge)
