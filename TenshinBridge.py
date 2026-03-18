import os
import sys
import json
import time
import socket
import asyncio
import threading
import subprocess
import requests
import qrcode
import tkinter as tk
from PIL import Image, ImageTk
from pathlib import Path
from websockets.server import serve
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

# Optional Zeroconf for local network discovery
try:
    from zeroconf import ServiceInfo, Zeroconf
    HAS_ZEROCONF = True
except ImportError:
    HAS_ZEROCONF = False

# --- UI CONSTANTS (TENSHIN PRO) ---
COLOR_ACCENT = "#00FFCC"
COLOR_HACKER = "#33FF00"
COLOR_BG = "#0A0A0A"
COLOR_SURFACE = "#141414"
COLOR_TEXT = "#E0E0E0"
COLOR_RED = "#FF4444"

# WebSocket moved to 8081 to avoid conflict with the HTTP server of the EXE helper (8080)
WS_PORT = 8081
HTTP_PORT = 8080
HELPER_EXE = "warframe-api-helper.exe"
INVENTORY_FILE = "inventory.json"
SAINAN_RELEASE_URL = "https://github.com/Sainan/warframe-api-helper/releases/download/1.1.1/warframe-api-helper.exe"

def resource_path(relative_path):
    """ Get absolute path to resource, works for dev and for PyInstaller """
    try:
        base_path = sys._MEIPASS
    except Exception:
        base_path = os.path.abspath(".")
    return os.path.join(base_path, relative_path)

class TenshinBridge:
    def __init__(self, ui_callback=None):
        self.clients = set()
        self.loop = None
        self.inventory_path = Path(INVENTORY_FILE)
        self.ip = self.get_ip()
        self.running = True
        self.ui_callback = ui_callback

    def get_ip(self):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        try:
            # doesn't even have to be reachable
            s.connect(('10.254.254.254', 1))
            ip = s.getsockname()[0]
        except Exception:
            ip = '127.0.0.1'
        finally:
            s.close()
        return ip

    async def handler(self, websocket):
        self.clients.add(websocket)
        if self.ui_callback:
            self.ui_callback(f"VÍNCULO ACTIVO: {websocket.remote_address[0]}")

        try:
            await websocket.send(json.dumps({"type": "system_status", "status": "HACKED_1999"}))
            if self.inventory_path.exists():
                data = self.inventory_path.read_text(encoding='utf-8')
                await websocket.send(json.dumps({
                    "type": "inventory",
                    "payload": json.loads(data),
                    "timestamp": time.time()
                }))
            async for message in websocket:
                pass
        except Exception:
            pass
        finally:
            if websocket in self.clients:
                self.clients.remove(websocket)
            if not self.clients and self.ui_callback:
                self.ui_callback("ESPERANDO TENNO...")

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
            for client in list(self.clients):
                try:
                    await client.send(payload)
                except:
                    if client in self.clients:
                        self.clients.remove(client)
        except Exception:
            pass

    def start_ws_server(self):
        self.loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self.loop)

        async def main_ws():
            async with serve(self.handler, "0.0.0.0", WS_PORT):
                while self.running:
                    await asyncio.sleep(1)

        try:
            self.loop.run_until_complete(main_ws())
        except Exception:
            pass

    def run_sainan_helper(self):
        if not os.path.exists(HELPER_EXE):
            try:
                r = requests.get(SAINAN_RELEASE_URL, timeout=10)
                with open(HELPER_EXE, 'wb') as f:
                    f.write(r.content)
            except:
                return

        try:
            subprocess.Popen([HELPER_EXE], creationflags=subprocess.CREATE_NO_WINDOW if os.name == 'nt' else 0)
        except:
            pass

    def stop(self):
        self.running = False
        if self.loop:
            self.loop.call_soon_threadsafe(self.loop.stop)

class GUI:
    def __init__(self, bridge):
        self.bridge = bridge
        self.root = tk.Tk()
        self.root.title("TENSHIN BRIDGE")
        self.root.geometry("420x620")
        self.root.configure(bg=COLOR_BG)
        self.root.resizable(False, False)

        # Main Container
        main_frame = tk.Frame(self.root, bg=COLOR_BG)
        main_frame.pack(expand=True, fill="both")

        # Handle Icon
        icon_path = resource_path('tenshin.ico')
        if os.path.exists(icon_path):
            try: self.root.iconbitmap(icon_path)
            except: pass

        header = tk.Frame(main_frame, bg=COLOR_SURFACE, height=100)
        header.pack(fill="x", side="top")
        header.pack_propagate(False)

        tk.Label(header, text="TENSHIN BRIDGE", fg=COLOR_ACCENT, bg=COLOR_SURFACE,
                 font=("Verdana", 18, "bold")).pack(pady=(20, 0))
        tk.Label(header, text="VINCULACIÓN NEURONAL ACTIVA", fg=COLOR_ACCENT, bg=COLOR_SURFACE,
                 font=("Verdana", 9)).pack()

        # QR Container
        qr_frame = tk.Frame(main_frame, bg=COLOR_BG, pady=20)
        qr_frame.pack()

        self.qr_label = tk.Label(qr_frame, bg=COLOR_BG, highlightthickness=2, highlightbackground=COLOR_ACCENT)
        self.qr_label.pack(padx=10, pady=10)
        self.generate_qr()

        # Info
        tk.Label(main_frame, text=f"IDENTIFICADOR: {self.bridge.ip}", fg=COLOR_TEXT, bg=COLOR_BG,
                 font=("Consolas", 12, "bold")).pack()
        tk.Label(main_frame, text="Escanea este código desde la App Tenshin", fg="#707070", bg=COLOR_BG,
                 font=("Verdana", 9)).pack(pady=5)

        # Action Buttons
        btn_frame = tk.Frame(main_frame, bg=COLOR_BG)
        btn_frame.pack(pady=20)

        def make_btn(parent, text, color, cmd):
            return tk.Button(parent, text=text, command=cmd, bg=COLOR_SURFACE, fg=color,
                             font=("Verdana", 10, "bold"), width=20, pady=10, relief="flat",
                             activebackground="#222222", activeforeground=color, cursor="hand2")

        make_btn(btn_frame, "SINCRONIZAR AHORA", COLOR_ACCENT, self.bridge.run_sainan_helper).pack(pady=8)
        make_btn(btn_frame, "DESCONECTAR", COLOR_RED, self.on_close).pack(pady=8)

        # Footer Status
        self.footer = tk.Frame(self.root, bg=COLOR_SURFACE, height=40)
        self.footer.pack(fill="x", side="bottom")
        self.footer.pack_propagate(False)

        self.status_var = tk.StringVar(value="● ESTADO: ESPERANDO TENNO...")
        tk.Label(self.footer, textvariable=self.status_var, fg=COLOR_HACKER, bg=COLOR_SURFACE,
                 font=("Courier", 9, "bold")).pack(pady=10)

        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

    def update_status(self, text):
        self.status_var.set(f"● ESTADO: {text.upper()}")

    def on_close(self):
        self.bridge.stop()
        self.root.destroy()
        sys.exit(0)

    def generate_qr(self):
        try:
            qr = qrcode.QRCode(version=1, box_size=10, border=2)
            # Enviar IP y puertos duales para evitar error 426
            qr.add_data(f"tenshin://sync?ip={self.bridge.ip}&ws={WS_PORT}&http={HTTP_PORT}")
            qr.make(fit=True)
            img = qr.make_image(fill_color="black", back_color="white").convert('RGB')

            data = img.getdata()
            new_data = []
            for item in data:
                if item[0] < 128: new_data.append((0, 255, 204)) # COLOR_ACCENT
                else: new_data.append((10, 10, 10)) # COLOR_BG
            img.putdata(new_data)

            img = img.resize((240, 240), Image.Resampling.LANCZOS)
            self.photo = ImageTk.PhotoImage(img)
            self.qr_label.config(image=self.photo)
        except:
            pass

    def run(self):
        self.root.mainloop()

class InventoryHandler(FileSystemEventHandler):
    def __init__(self, bridge):
        self.bridge = bridge
    def on_modified(self, event):
        if not event.is_directory and event.src_path.endswith(INVENTORY_FILE):
            if self.bridge.loop:
                self.bridge.loop.call_soon_threadsafe(
                    lambda: asyncio.ensure_future(self.bridge.broadcast_inventory(), loop=self.bridge.loop)
                )

if __name__ == "__main__":
    if getattr(sys, 'frozen', False):
        sys.stdout = open(os.devnull, 'w')
        sys.stderr = open(os.devnull, 'w')

    bridge = TenshinBridge()
    app = GUI(bridge)
    bridge.ui_callback = app.update_status

    bridge.run_sainan_helper()

    if HAS_ZEROCONF:
        try:
            zeroconf = Zeroconf()
            info = ServiceInfo("_tenshin._tcp.local.", "TenshinBridge._tenshin._tcp.local.",
                               addresses=[socket.inet_aton(bridge.ip)], port=WS_PORT)
            zeroconf.register_service(info)
        except Exception:
            pass

    observer = Observer()
    observer.schedule(InventoryHandler(bridge), path='.', recursive=False)
    observer.start()

    threading.Thread(target=bridge.start_ws_server, daemon=True).start()
    app.run()
