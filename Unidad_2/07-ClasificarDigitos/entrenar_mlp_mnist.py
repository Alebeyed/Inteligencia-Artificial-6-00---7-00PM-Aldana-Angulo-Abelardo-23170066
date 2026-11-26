import os
import tensorflow as tf
import matplotlib.pyplot as plt
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Input, Flatten, Dense


def main():
    carpeta_actual = os.path.dirname(os.path.abspath(__file__))

    # 1. Cargar dataset MNIST (60,000 entrenamiento / 10,000 prueba)
    (x_train, y_train), (x_test, y_test) = tf.keras.datasets.mnist.load_data()

    # Normalizar valores de píxeles de [0, 255] a [0.0, 1.0]
    x_train = x_train.astype("float32") / 255.0
    x_test = x_test.astype("float32") / 255.0

    # 2. Construir arquitectura MLP (Perceptrón Multicapa)
    modelo = Sequential([
        Input(shape=(28, 28)),
        Flatten(),
        Dense(256, activation="relu"),
        Dense(128, activation="relu"),
        Dense(10, activation="softmax")
    ])

    modelo.compile(
        optimizer="adam",
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"]
    )

    # 3. Entrenar el modelo
    print("Entrenando Perceptrón Multicapa (MLP) con dataset MNIST...")
    historial = modelo.fit(
        x_train, y_train,
        epochs=10,
        batch_size=512,
        validation_split=0.2
    )

    # 4. Evaluar en conjunto de prueba
    loss, accuracy = modelo.evaluate(x_test, y_test, verbose=0)
    print(f"\nExactitud en prueba: {accuracy * 100:.2f}% | Pérdida: {loss:.4f}")

    # 5. Guardar el modelo entrenado (Requisito 2)
    ruta_modelo = os.path.join(carpeta_actual, "modelo_mlp_mnist.keras")
    modelo.save(ruta_modelo)
    print(f"Modelo guardado en: {ruta_modelo}")

    # 6. Guardar gráfica de entrenamiento y validación
    plt.figure(figsize=(11, 4.5))

    plt.subplot(1, 2, 1)
    plt.plot(historial.history["accuracy"], label="Entrenamiento", color="royalblue")
    plt.plot(historial.history["val_accuracy"], label="Validación", color="darkorange")
    plt.title("Exactitud del Modelo (Accuracy)")
    plt.xlabel("Épocas")
    plt.ylabel("Accuracy")
    plt.legend()
    plt.grid(True, linestyle="--", alpha=0.6)

    plt.subplot(1, 2, 2)
    plt.plot(historial.history["loss"], label="Entrenamiento", color="royalblue")
    plt.plot(historial.history["val_loss"], label="Validación", color="darkorange")
    plt.title("Pérdida del Modelo (Loss)")
    plt.xlabel("Épocas")
    plt.ylabel("Loss")
    plt.legend()
    plt.grid(True, linestyle="--", alpha=0.6)

    plt.tight_layout()
    ruta_grafica = os.path.join(carpeta_actual, "desempeno_mlp_mnist.png")
    plt.savefig(ruta_grafica, dpi=150)
    plt.close()
    print(f"Gráfica guardada en: {ruta_grafica}")


if __name__ == "__main__":
    main()