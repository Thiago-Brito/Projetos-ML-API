from flask import Flask, render_template, request, jsonify
from openai import OpenAI
from dotenv import load_dotenv
import os
from time import sleep
from helpers import carrega  
import json

load_dotenv()


client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key= "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"  # Use a variável de ambiente para segurança
)

app = Flask(__name__)
app.secret_key = 'alura'


contexto = carrega("dados/ecomart.txt")  

modelo = "deepseek/deepseek-r1:free"  

def bot(prompt):
    maximo_tentativas = 1
    repeticao = 0

    while True:
        try:
            prompt_do_sistema = f"""
            Você é um chatbot de atendimento a clientes de um e-commerce. 
            Você não deve responder perguntas que não sejam sobre o e-commerce informado!
            Você deve gerar respostas utilizando o contexto abaixo.
            
            # Contexto
            {contexto}
            """

            response = client.chat.completions.create(
                messages=[
                    {"role": "system", "content": prompt_do_sistema},
                    {"role": "user", "content": prompt}
                ],
                temperature=1,
                max_tokens=300,
                top_p=1,
                frequency_penalty=0,
                presence_penalty=0,
                model=modelo
            )
            
            return response.choices[0].message.content  # Retorna apenas o texto da resposta

        except Exception as erro:
            repeticao += 1
            if repeticao >= maximo_tentativas:
                return f"Erro no GPT: {erro}"
            print('Erro de comunicação com OpenAI:', erro)
            sleep(1)

# Rota para processar mensagens do chatbot
@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()
    prompt = data.get("msg")

    if not prompt:
        return jsonify({"error": "Mensagem não informada"}), 400

    resposta = bot(prompt)

    # Força a decodificação para evitar caracteres Unicode
    resposta_decodificada = json.loads(json.dumps(resposta, ensure_ascii=False))

    return jsonify({"resposta": resposta_decodificada})

# Rota da página inicial
@app.route("/")
def home():
    return render_template("index.html")

if __name__ == "__main__":
    app.run(debug=True)
