from flask import Flask, render_template, request, jsonify
from langchain_core.prompts import PromptTemplate
from langchain_ollama import OllamaLLM
from langchain.prompts import PromptTemplate
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain
import json
from helpers import *



# 🔹 Inicializa Flask
app = Flask(__name__)
app.secret_key = "ecomart"

modelo = OllamaLLM(model="llama3.1", temperature=0.5, max_tokens=300,top_p=0.9, 
                   frequency_penalty=0.0, presence_penalty=0.0)

modelo_do_prompt = PromptTemplate.from_template(
    """
        Você é um chatbot de atendimento a clientes de um e-commerce. 
        Você não deve responder perguntas que não sejam sobre o e-commerce informado!
        Você deve gerar respostas utilizando o contexto abaixo.

        # Contexto
        {contexto}
         # Pergunta do cliente:
        {pergunta}
    """
)

#memory = ConversationBufferMemory()

#conversation = ConversationChain(llm=modelo,
#                                verbose=True,
#                                memory=memory)


contexto = carrega("dados/ecomart.txt")  

def bot(pergunta):
    #resposta = conversation.predict(input=pergunta)
    prompt = modelo_do_prompt.format(contexto=contexto, pergunta=pergunta)
    resposta = modelo.invoke(prompt)
    return resposta.strip()

# 🔹 Rota para processar mensagens do chatbot

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json()
    pergunta = data.get("msg")

    if not pergunta:
        return jsonify({"error": "Mensagem não informada"}), 400

    resposta = bot(pergunta)

    
    resposta_decodificada = json.loads(json.dumps(resposta, ensure_ascii=False))

    return resposta_decodificada


# 🔹 Rota da página inicial
@app.route("/")
def home():
    return render_template("index.html")

if __name__ == "__main__":
    app.run(debug=True)