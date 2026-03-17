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

# Configuración
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
        print(f"📱 Celular conectado desde {websocket.remote_address}")

        # Enviar señal de "HACKED" al conectar por primera vez
        await websocket.send(json.dumps({"type": "system_status", "status": "HACKED_1999"}))

        try:
            if self.inventory_path.exists():
                data = self.inventory_path.read_text(encoding='utf-8')
                await websocket.send(json.dumps({"type": "inventory", "payload": json.loads(data)}))

            async for message in websocket:
                pass # Mantener conexión viva
        finally:
            self.clients.remove(websocket)

    async def broadcast_inventory(self):
        if not self.clients or not self.inventory_path.exists():
            return
        try:
            data = json.loads(self.inventory_path.read_text(encoding='utf-8'))
            payload = json.dumps({"type": "inventory_update", "payload": data})
            for client in self.clients:
                await client.send(payload)
        except Exception as e:
            print(f"Error en broadcast: {e}")

    def start_ws_server(self):
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

        async def main_ws():
            async with serve(self.handler, "0.0.0.0", WS_PORT):
                await asyncio.Future() # run forever

        self.loop.run_until_complete(main_ws())

    def download_sainan_helper(self):
        if not os.path.exists(HELPER_EXE):
            print("💾 Descargando Warframe API Helper de Sainan...")
            r = requests.get(SAINAN_RELEASE_URL, allow_redirects=True)
            open(HELPER_EXE, 'wb').write(r.content)
            print("✅ Descarga completada.")

    def run_sainan_helper(self):
        print("🚀 Iniciando API Helper...")
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
    # Ícono estilo terminal retro
    img = Image.new('RGB', (64, 64), (5, 5, 5))
    draw = ImageDraw.Draw(img)
    draw.text((10, 10), "1999", fill=(0, 255, 65))

    def quit_app(icon):
        icon.stop()
        os._exit(0)

    icon = pystray.Icon("TenshinBridge", img, "TENSHIN BRIDGE: 1999", menu=pystray.Menu(
        pystray.MenuItem(f"IP: {bridge.get_ip()}", lambda: None, enabled=False),
        pystray.MenuItem("Forzar Sincronización", bridge.run_sainan_helper),
        pystray.MenuItem("Salir", quit_app)
    ))
    icon.run()

if __name__ == "__main__":
    print("═" * 40)
    print("  SISTEMA HÖLLVANIA - PROTOCOLO 1999")
    print("═" * 40)

    bridge = TenshinBridge()
    bridge.download_sainan_helper()
    bridge.run_sainan_helper()

    # Zeroconf Discovery
    zeroconf = Zeroconf()
    info = ServiceInfo("_tenshin._tcp.local.", "TenshinBridge._tenshin._tcp.local.",
                       addresses=[socket.inet_aton(bridge.get_ip())], port=WS_PORT)
    zeroconf.register_service(info)

    # File Watcher
    observer = Observer()
    observer.schedule(InventoryHandler(bridge), path='.', recursive=False)
    observer.start()

    # Servidor en thread separado
    threading.Thread(target=bridge.start_ws_server, daemon=True).start()

    print(f"📡 Esperando conexión en {bridge.get_ip()}:{WS_PORT}...")
    run_tray(bridge)
