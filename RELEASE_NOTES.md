# 🚀 Release: v1.1.3 — The Void Trader Update

**Nombre de la Release:** `v1.1.3 - Smart Sync & Baro Ki'Teer`

## 📝 Descripción de los cambios

Esta versión marca un hito en la estabilidad y funcionalidad de **Tenshin**, introduciendo la integración de datos en tiempo real y una experiencia de sincronización mucho más fluida.

### 🔄 Sincronización Inteligente (Smart Sync)
*   **Auto-Discovery:** La aplicación ahora es capaz de escanear tu red local para encontrar automáticamente la IP de tu PC ejecutando el *Warframe Helper*. Ya no es estrictamente necesario configurar la IP manualmente.
*   **Feedback en Tiempo Real:** Se ha implementado un sistema de **Snackbars** y estados visuales en el botón de Sync para informar sobre errores de conexión o progreso de la carga.

### 👤 Baro Ki'Teer (Comerciante del Vacío)
*   **Pantalla Completa:** Nueva interfaz premium para Baro Ki'Teer.
*   **Datos Reales:** Integración con la API de *WarframeStat* para mostrar la ubicación exacta, el tiempo de llegada/partida y el inventario actual (o estimado si no está presente).
*   **Recomendaciones:** Etiquetas dinámicas (`INDISPENSABLE`, `ESPECIAL`) para ayudarte a decidir en qué gastar tus ducados.

### 🛠️ Correcciones y Mejoras
*   **Fix Crítico de Compilación:** Se solucionó un error en el `NavGraph.kt` que impedía generar el APK debido a parámetros de navegación obsoletos.
*   **Optimización de Red:** Se incrementaron los timeouts para mejorar la compatibilidad con redes Wi-Fi inestables.
*   **Actualización de Versión:** Incremento a `versionCode 5` para asegurar la correcta actualización sobre versiones instaladas.

---

## 📦 Información del APK

*   **Archivo:** `app-debug.apk`
*   **Versión:** `1.1.3 (5)`
*   **Estado:** Compilación verificada exitosamente.

---

### 💡 Instrucciones de uso para la Sincronización:
1. Conecta tu móvil a la misma red Wi-Fi que tu PC.
2. Abre la app y presiona el botón **"⟳ sync"** en la barra superior.
3. Tenshin buscará tu PC automáticamente y traerá tu arsenal actualizado.
