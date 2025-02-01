from flask import Flask, request, jsonify
from flask_basicauth import BasicAuth
from textblob import TextBlob
from sklearn.linear_model import LinearRegression
import pickle
from googletrans import Translator

colunas = ['tamanho','ano','garagem']
modelo = pickle.load(open('modelo.sav','rb'))

app = Flask(__name__)
app.config['BASIC_AUTH_USERNAME'] = 'julio'
app.config['BASIC_AUTH_PASSWORD'] = 'alura'

basic_auth = BasicAuth(app)

@app.route('/')
def home():
    return "Minha primeira API."

@app.route('/sentimento/<frase>')
@basic_auth.required
def sentimento(frase):
    translator = Translator()
    frase_en = translator.translate(frase, dest='en')
    tb_en = TextBlob(frase_en.text)
    polaridade = tb_en.sentiment.polarity
    return "polaridade: {}".format(polaridade)

@app.route('/cotacao/', methods=['POST'])
def cotacao():
    dados = request.get_json()
    dados_input = [dados[col] for col in colunas]
    preco = modelo.predict([dados_input])
    return jsonify(preco=preco[0])

app.run(debug=True)