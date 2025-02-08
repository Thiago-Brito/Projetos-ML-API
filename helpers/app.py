import torch
from PIL import Image
import fitz  # PyMuPDF
import pytesseract
from transformers import BlipProcessor, BlipForConditionalGeneration

# 🔹 Carrega modelos de IA
device = "cuda" if torch.cuda.is_available() else "cpu"

# Modelo para imagens (BLIP)
blip_processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
blip_model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base").to(device)

# 🔹 Função para extrair texto de PDF (corrigida)
def extract_text_from_pdf(pdf_path):
    with open(pdf_path, "rb") as pdf_file:  
        doc = fitz.open(stream=pdf_file.read(), filetype="pdf")
        text = "\n".join([page.get_text() for page in doc])
        return text if text else "Nenhum texto encontrado no PDF."

# Função para descrever a imagem
def describe_image(image_path):
    image = Image.open(image_path).convert("RGB")
    inputs = blip_processor(image, return_tensors="pt").to(device)
    out = blip_model.generate(**inputs)
    return blip_processor.decode(out[0], skip_special_tokens=True)

# Função para extrair texto da imagem
def extract_text_from_image(image_path):
    image = Image.open(image_path)
    text = pytesseract.image_to_string(image)
    return text.strip() if text.strip() else "Nenhum texto detectado."

# 🚀 Exemplo de uso (corrigido)
pdf_text = extract_text_from_pdf("thiagoBarbosa.pdf")
print(f"📄 Texto do PDF:\n{pdf_text}")

image_caption = describe_image("image.jpg")
print(f"🖼️ Descrição da Imagem: {image_caption}, o texto da imagem{extract_text_from_image("image.jpg")}")
