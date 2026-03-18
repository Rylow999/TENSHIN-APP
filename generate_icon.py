from PIL import Image, ImageDraw

def create_tenshin_ico():
    # Dimensiones para el icono, incluyendo versiones más pequeñas
    sizes = [(256, 256), (128, 128), (64, 64), (48, 48), (32, 32), (16, 16)]

    # Crear la imagen base con fondo transparente
    base_img = Image.new('RGBA', (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(base_img)

    # Colores base de Tenshin
    color_accent = (0, 255, 204, 255) # Cian vibrante

    # Dibujar un borde exterior (ej: círculo o hexágono, aquí un círculo simple)
    draw.ellipse([20, 20, 236, 236], outline=color_accent, width=10)

    # Dibujar una forma que represente la 'T' de Tenshin o un símbolo de red
    # Esto es un ejemplo, puedes ajustarlo para que coincida más con tu logo
    draw.line([128, 60, 128, 196], fill=color_accent, width=15)
    draw.line([60, 128, 196, 128], fill=color_accent, width=15)
    draw.line([80, 80, 176, 176], fill=color_accent, width=5)
    draw.line([80, 176, 176, 80], fill=color_accent, width=5)

    # Guardar la imagen como un archivo .ico con múltiples tamaños
    try:
        base_img.save('tenshin.ico', format='ICO', sizes=sizes)
        print("✅ tenshin.ico generado con éxito.")
    except Exception as e:
        print(f"❌ Error al generar tenshin.ico: {e}")

if __name__ == "__main__":
    create_tenshin_ico()
