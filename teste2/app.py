from flask import Flask, request, jsonify, render_template, Response
import requests
from helpers import *
import json
from deep_translator import GoogleTranslator

API_KEY = ""
MODEL_NAME = "HuggingFaceH4/zephyr-7b-beta"

app = Flask(__name__)
app.secret_key = 'alura'

historico_conversas = {}  # Dicionário para armazenar conversas por usuário
contexto = carrega("dados/ecomart.txt")

translator_en = GoogleTranslator(source='auto', target='en')
translator_pt = GoogleTranslator(source='auto', target='pt')


def bot(user_id, prompt):

    prompt_do_sistema = f"""
    You are a customer service chatbot for an e-commerce store called EcoMart.
    You **can only** answer questions about EcoMart, its products, and its services.

    If the customer asks something irrelevant, politely inform them that you cannot help.
    Use a polite and objective tone.
    You must generate responses using the context below.

    # Context:
    {contexto}

    Now, continue the conversation with the customer.

    Customer: {prompt}
    Chatbot:
    """

    payload = {
        "inputs": prompt_do_sistema,
        "parameters": {"max_new_tokens": 300, "temperature": 0.1}
    }

    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    response = requests.post(
        f"https://api-inference.huggingface.co/models/{MODEL_NAME}",
        headers=headers,
        json=payload
    )

    if response.status_code == 200:
        bot_response = response.json()[0]["generated_text"].split("Chatbot:")[-1].strip()
        return bot_response
    else:
        return f"Erro na API: {response.json()}"

@app.route("/chat", methods=["POST"])
def chat():
    data = request.json
    user_id = data.get("user_id", "default_user")
    prompt = data.get("msg", "")
    prompt_eng = translator_en.translate(prompt)

    resposta = bot(user_id, prompt_eng)

    resposta_pt = translator_pt.translate(resposta)

    return resposta_pt


@app.route("/")
def home():
    return render_template("index.html")

if __name__ == "__main__":
    app.run(debug=True)
