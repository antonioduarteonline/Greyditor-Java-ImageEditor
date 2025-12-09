# 🎨 Greyditor Java Image Editor

Projeto desenvolvido na Unidade Curricular **Introdução à Programação (IPA)** - ISCTE.

Esta aplicação utiliza a biblioteca **Greyditor** para manipular imagens em tons de cinzento, permitindo aplicar diversos **filtros, efeitos e operações sobre a matriz da imagem** ao nível dos pixels.

---

## 🚀 Funcionalidades

### 🔹 Filtros (pixel a pixel)
- **Brighten** — clareia pixels individualmente
- **Darken** — escurece mantendo limites 0-255
- **Contrast** — intensifica pretos e brancos

### 🔹 Efeitos (transformação da matriz)
- **Mirror Horizontal / Vertical**
- **Grain** — ruído aleatório com intensidade ajustável
- **Margem** — adiciona moldura branca
- **Vinheta** — escurece exterior, mantendo centro destacado
- **Antigo** — composição de efeitos (grain + vinheta + margem)
- **Retro** — contraste mais forte + blur + vinheta

### 🔹 Operações sobre a imagem
- **Crop** — recorta área selecionada
- **Expand** — aumenta tamanho mantendo conteúdo original
- **Posterizar** — reduz número de níveis de cinzento
- **Rodar** — rotação 90º
- **Blur** — efeito de média dos vizinhos
- **Copiar / Cut / Paste** — edição de áreas
- **Undo** — reverte última alteração

---

## 🧠 Técnicas utilizadas

- Manipulação de **matrizes bidimensionais**
- Processamento de imagem em Java
- Armazenamento de estados (clipboard, undo)
- Programação orientada a objetos
- Verificação de limites para evitar erros (`ArrayIndexOutOfBounds`)

---

## 📸 Demonstrações

| Efeito | Antes | Depois |
|--------|-------|--------|
| Mirror Horizontal | ![before](assets/sample.jpg) | _resultado_ |
| Posterizar | ![before](assets/sample.jpg) | _resultado_ |

> Podes substituir por imagens antes/depois dos efeitos aplicados.

---

## ▶️ Como executar

1. Clona este repositório:
```bash
git clone https://github.com/antonioduarteonline/Greyditor-Java-ImageEditor.git
