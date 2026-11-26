import os
import sys
import numpy as np
import tensorflow as tf

carpeta_actual = os.path.dirname(os.path.abspath(__file__))
ruta_modelo = os.path.join(carpeta_actual, "modelo_mlp_mnist.keras")

if not os.path.exists(ruta_modelo):
    print("No se encontró 'modelo_mlp_mnist.keras'. Ejecuta primero 'entrenar_mlp_mnist.py'.")
    sys.exit(1)

MODELO = tf.keras.models.load_model(ruta_modelo)


def ejecutar_en_consola():
    import matplotlib.pyplot as plt

    print("\n=== APLICACIÓN DE CLASIFICACIÓN MNIST EN TIEMPO REAL ===")
    (_, _), (x_test, y_test) = tf.keras.datasets.mnist.load_data()
    x_norm = x_test.astype("float32") / 255.0

    while True:
        dato = input("\nEscribe un índice de imagen (0-9999) para predecir o 's' para salir: ").strip()
        if dato.lower() == "s":
            break
        if not dato.isdigit() or not (0 <= int(dato) <= 9999):
            print("Ingresa un número entero válido entre 0 y 9999.")
            continue

        idx = int(dato)
        entrada = x_norm[idx : idx + 1]
        probabilidades = MODELO.predict(entrada, verbose=0)[0]
        prediccion = int(np.argmax(probabilidades))
        confianza = float(probabilidades[prediccion]) * 100

        print(f"Valor Real: {y_test[idx]}  -->  Predicción IA: {prediccion} (Confianza: {confianza:.2f}%)")

        plt.figure(figsize=(3, 3))
        plt.imshow(x_test[idx], cmap="gray")
        plt.title(f"Real: {y_test[idx]} | Pred: {prediccion} ({confianza:.1f}%)")
        plt.axis("off")
        ruta_img = os.path.join(carpeta_actual, "prediccion_actual.png")
        plt.savefig(ruta_img, bbox_inches="tight")
        plt.close()
        print(f"Imagen guardada en: {ruta_img}")


def ejecutar_ventana_dibujo():
    import tkinter as tk
    from PIL import Image, ImageDraw

    class PizarraDigitos:
        def __init__(self, root):
            self.root = root
            self.root.title("Clasificador MLP MNIST (0-9)")
            self.root.resizable(False, False)

            self.canvas = tk.Canvas(root, width=280, height=280, bg="black", cursor="cross")
            self.canvas.pack(pady=10, padx=10)

            self.img_pil = Image.new("L", (280, 280), color=0)
            self.draw = ImageDraw.Draw(self.img_pil)

            self.canvas.bind("<B1-Motion>", self.dibujar)
            self.canvas.bind("<ButtonRelease-1>", self.clasificar)

            self.etiqueta = tk.Label(
                root, text="Dibuja un dígito del 0 al 9", font=("Arial", 13, "bold")
            )
            self.etiqueta.pack(pady=5)

            tk.Button(
                root, text="Borrar Pizarra", command=self.borrar, font=("Arial", 11), bg="#d9534f", fg="white"
            ).pack(pady=8)

        def dibujar(self, evento):
            r = 10
            self.canvas.create_oval(
                evento.x - r, evento.y - r, evento.x + r, evento.y + r, fill="white", outline="white"
            )
            self.draw.ellipse([evento.x - r, evento.y - r, evento.x + r, evento.y + r], fill=255)

        def clasificar(self, _evento=None):
            img_28 = self.img_pil.resize((28, 28), Image.Resampling.LANCZOS)
            entrada = (np.array(img_28).astype("float32") / 255.0).reshape(1, 28, 28)

            probs = MODELO.predict(entrada, verbose=0)[0]
            digito = int(np.argmax(probs))
            confianza = float(probs[digito]) * 100
            self.etiqueta.config(text=f"Predicción: {digito}  ({confianza:.1f}%)")

        def borrar(self):
            self.canvas.delete("all")
            self.img_pil = Image.new("L", (280, 280), color=0)
            self.draw = ImageDraw.Draw(self.img_pil)
            self.etiqueta.config(text="Dibuja un dígito del 0 al 9")

    ventana = tk.Tk()
    PizarraDigitos(ventana)
    ventana.mainloop()


if __name__ == "__main__":
    if os.environ.get("DISPLAY") or sys.platform == "win32":
        ejecutar_ventana_dibujo()
    else:
        ejecutar_en_consola()