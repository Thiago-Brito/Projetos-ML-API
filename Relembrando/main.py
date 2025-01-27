"""
- A aplicação deve iniciar mostrando uma lista de opções do que é possível fazer com o app e permitir que o usuário digite uma escolha para iniciar a aplicação.
- Deve ser possível adicionar um contato
    - O contato pode ter os dados:
    - Nome
    - Telefone
    - Email
    - Favorito (está opção é para poder marcar um contato como favorito)
- Deve ser possível visualizar a lista de contatos cadastrados
- Deve ser possível editar um contato
- Deve ser possível marcar/desmarcar um contato como favorito
- Deve ser possível ver uma lista de contatos favoritos
- Deve ser possível apagar um contato

"""
def criar_contato(nome,telefone,email,favorito=False):
    contato = {"Nome": nome, "Telefone": telefone, "Email": email, "Favorito": favorito}
    contatos.append(contato)
    return

def visualizar_contatos():
    print("\n\n")
    for indice, valor in enumerate(contatos):
        nome = valor["Nome"]
        telefone = valor["Telefone"]
        email = valor["Email"]
        favorito = "✓" if valor["Favorito"] else " "
        print(f"{indice+1}- Nome: {nome}, Telefone: {telefone}, Email: {email}, Favorito: {favorito}")
    return

def editar_contato(indice,editar,nova_info):
    if editar == 1:
        contatos[indice-1]["Nome"] = nova_info
    elif editar == 2:
        contatos[indice-1]["Telefone"] = nova_info
    elif editar == 3:
        contatos[indice-1]["Email"] = nova_info
    print("\n Contato alterado")
    return

def marcar_desmarcar_favoritos(indice):
    contatos[indice-1]["Favorito"] = not contatos[indice-1]["Favorito"]
    return

def visualizar_favoritos():
    print("\n\n")
    for indice, valor in enumerate(contatos):
        if valor["Favorito"]:
            nome = valor["Nome"]
            telefone = valor["Telefone"]
            email = valor["Email"]
            favorito = "✓" if valor["Favorito"] else " "
            print(f"{indice+1}- Nome: {nome}, Telefone: {telefone}, Email: {email}, Favorito: {favorito}")
    return

def deletando_contato(indice):
    del contatos[indice-1]
    return
contatos = []

while True:
    print("\n\nEscolha uma das opções abaixo")
    print("1-Criar um novo contanto")
    print("2-visualizar os contatos cadastrados")
    print("3-Editar um contato")
    print("4-marca/desmarcar um contato como favorito")
    print("5-visualizar contatos favoritos")
    print("6-Apagar um contato")
    print("7-sair do sistema")
    escolha = int(input("digite sua opção "))
    
    if escolha == 1:
        print("digite as informações abaixo para criar um contato ")
        nome = input("Digite o nome do contato ")
        telefone = input("Digite o telefone do contato ")
        email = input("Digite o email do contato ")
        criar_contato(nome,telefone,email)
    elif escolha == 2:
        visualizar_contatos()
    elif escolha == 3:
        visualizar_contatos()
        indice = int(input("Digite o indice do contato que voce quer editar "))
        editar = int(input("Escolha o que voce quer editar 1-Nome, 2-Telefone, 3-Email "))
        nova_info = input("Digite o novo conteudo: ")
        editar_contato(indice,editar,nova_info)
        visualizar_contatos()
    elif escolha == 4:
        visualizar_contatos()
        indice = int(input("Digite o indice do contato que voce quer marca/desmarcar como favorito "))
        marcar_desmarcar_favoritos(indice)
        visualizar_contatos()
    elif escolha == 5:
        visualizar_favoritos()
    elif escolha == 6:
        visualizar_contatos()
        indice = int(input("Digite o indice do contato que voce quer remover "))
        deletando_contato(indice)
    elif escolha == 7:
        break
