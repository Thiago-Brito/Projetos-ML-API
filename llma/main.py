from langchain_ollama import OllamaLLM

modelo = OllamaLLM(model="llama3.1")

resposta = modelo.invoke("Ola, como você está?")

print(resposta) # "Estou bem, obrigado!"