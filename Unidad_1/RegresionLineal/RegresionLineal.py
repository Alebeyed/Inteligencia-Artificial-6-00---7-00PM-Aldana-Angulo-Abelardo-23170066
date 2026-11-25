import numpy as np
import matplotlib.pyplot as plt
from sklearn.datasets import fetch_california_housing
from sklearn.model_selection import train_test_split


class RegresorLinealGD:

    def __init__(self, tasa_aprendizaje=0.05, iteraciones=1000):
        self.tasa_aprendizaje = tasa_aprendizaje
        self.iteraciones = iteraciones
        self.pendiente = 0.0
        self.intercepto = 0.0
        self.media_x = 0.0
        self.std_x = 1.0
        self.curva_perdida = []

    def _estandarizar(self, x):
        return (x - self.media_x) / self.std_x

    def entrenar(self, x_entrenamiento, y_entrenamiento):
        self.media_x = np.mean(x_entrenamiento)
        self.std_x = np.std(x_entrenamiento)
        x_norm = self._estandarizar(x_entrenamiento)

        m = len(y_entrenamiento)
        self.pendiente = 0.0
        self.intercepto = 0.0
        self.curva_perdida = []

        for _ in range(self.iteraciones):
            y_estimada = self.pendiente * x_norm + self.intercepto
            residuo = y_estimada - y_entrenamiento

            # Función de pérdida: Error Cuadrático Medio (MSE)
            mse = np.mean(residuo ** 2)
            self.curva_perdida.append(mse)

            # Derivadas parciales del MSE
            grad_pendiente = (2 / m) * np.dot(residuo, x_norm)
            grad_intercepto = (2 / m) * np.sum(residuo)

            # Actualización simultánea de parámetros
            self.pendiente -= self.tasa_aprendizaje * grad_pendiente
            self.intercepto -= self.tasa_aprendizaje * grad_intercepto

    def predecir(self, x):
        x_norm = self._estandarizar(x)
        return self.pendiente * x_norm + self.intercepto

    def obtener_ecuacion_original(self):
        """Convierte los parámetros normalizados a la escala original del dataset."""
        m_real = self.pendiente / self.std_x
        b_real = self.intercepto - (self.pendiente * self.media_x / self.std_x)
        return m_real, b_real

    def evaluar(self, x_prueba, y_prueba):
        predicciones = self.predecir(x_prueba)
        mse = np.mean((y_prueba - predicciones) ** 2)
        rmse = np.sqrt(mse)

        ss_residual = np.sum((y_prueba - predicciones) ** 2)
        ss_total = np.sum((y_prueba - np.mean(y_prueba)) ** 2)
        r2 = 1.0 - (ss_residual / ss_total)
        return mse, rmse, r2


def main():
    # 1. Cargar el dataset completo de California Housing (20,640 registros)
    dataset = fetch_california_housing()
    # Columna 0 corresponde a 'MedInc' (Ingreso medio del bloque en decenas de miles de USD)
    X = dataset.data[:, 0]
    y = dataset.target  # Valor mediano de la vivienda en cientos de miles de USD

    # 2. Dividir en entrenamiento (80%) y prueba (20%)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, random_state=42
    )

    print(f"Total de registros cargados: {len(X):,}")
    print(f"Muestras de entrenamiento: {len(X_train):,} | Muestras de prueba: {len(X_test):,}\n")

    # 3. Instanciar y entrenar el modelo
    modelo = RegresorLinealGD(tasa_aprendizaje=0.05, iteraciones=800)
    modelo.entrenar(X_train, y_train)

    m_orig, b_orig = modelo.obtener_ecuacion_original()
    mse, rmse, r2 = modelo.evaluar(X_test, y_test)

    print("=== RESULTADOS DEL MODELO (CONJUNTO DE PRUEBA) ===")
    print(f"Ecuación ajustada: Precio = {m_orig:.4f} * (MedInc) + {b_orig:.4f}")
    print(f"Error Cuadrático Medio (MSE):  {mse:.4f}")
    print(f"Raíz del Error (RMSE):         {rmse:.4f} (${rmse * 100_000:,.2f} USD)")
    print(f"Coeficiente R² (Prueba):       {r2:.4f}\n")

    # 4. Inferencia con casos de prueba nuevos
    print("=== EJEMPLOS DE PREDICCIÓN ===")
    ingresos_ejemplo = np.array([2.5, 5.0, 8.5])  # $25k, $50k y $85k anuales
    estimaciones = modelo.predecir(ingresos_ejemplo)
    for ing, est in zip(ingresos_ejemplo, estimaciones):
        print(f"Ingreso medio: ${ing * 10_000:,.0f} USD -> Valor estimado vivienda: ${est * 100_000:,.2f} USD")

    # 5. Visualización comparativa en una sola ventana
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5.5))

    # Gráfica izquierda: Ajuste lineal sobre datos de prueba
    ax1.scatter(X_test, y_test, color="#2b5c8f", alpha=0.25, s=18, label="Datos de Prueba (20%)")
    eje_x = np.linspace(X.min(), X.max(), 200)
    ax1.plot(eje_x, modelo.predecir(eje_x), color="#d9381e", linewidth=2.5,
             label=f"Recta Ajustada: y = {m_orig:.2f}x + {b_orig:.2f}")
    ax1.set_title("Ajuste de Regresión Lineal (California Housing)", fontweight="bold")
    ax1.set_xlabel("Ingreso Medio (MedInc - decenas de miles USD)")
    ax1.set_ylabel("Valor de Vivienda (cientos de miles USD)")
    ax1.legend()
    ax1.grid(True, linestyle=":", alpha=0.7)

    # Gráfica derecha: Convergencia de la pérdida MSE
    ax2.plot(range(1, modelo.iteraciones + 1), modelo.curva_perdida, color="#1f8a70", linewidth=2.2)
    ax2.set_title("Convergencia del Descenso del Gradiente", fontweight="bold")
    ax2.set_xlabel("Iteración (Época)")
    ax2.set_ylabel("Error Cuadrático Medio (MSE)")
    ax2.grid(True, linestyle=":", alpha=0.7)

    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    main()