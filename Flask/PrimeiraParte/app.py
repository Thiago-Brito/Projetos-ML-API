from flask import Flask, request, jsonify
from models.user import User
from models.tasks import Task
from database import db
from flask_login import LoginManager, login_user, current_user, logout_user, login_required
import bcrypt

app = Flask(__name__)

app.config['SECRET_KEY'] = "your_secret_key"
app.config['SQLALCHEMY_DATABASE_URI'] = "mysql+pymysql://root:admin123@127.0.0.1:3306/flask-crud"

login_manager = LoginManager()
db.init_app(app)
login_manager.init_app(app)
#view login
login_manager.login_view = 'login' #end do login


@login_manager.user_loader
def load_user(user_id):
  return User.query.get(user_id)

@app.route('/login', methods=["POST"])
def login():
  data = request.json
  username = data.get("username")
  password = data.get("password")
  if username and password:
    user = User.query.filter_by(username = username).first()
    if user and bcrypt.checkpw(str.encode(password), str.encode(user.password)):
      login_user(user)
      return jsonify({"message": "Autenticação realizada com sucesso!"})
  return jsonify({"message": "Credências realizadas com sucesso"})

@app.route('/logout', methods=['GET'])
@login_required
def logout():
  logout_user()
  return jsonify({"message": "Logout realizado com sucesso!"})

@app.route('/user', methods=["POST"])
def create_user():
  data = request.json
  username = data.get("username")
  password = data.get("password")

  if username and password:
    hashed_password = bcrypt.hashpw(str.encode(password), bcrypt.gensalt())
    user = User(username=username, password=hashed_password, role='user')
    db.session.add(user)
    db.session.commit()
    return jsonify({"message": "Usuario cadastrado com sucesso"})

  return jsonify({"message": "Dados invalidos"}), 400


@app.route("/task", methods=["POST"])
def create_task():
  data = request.json
  title = data.get("title")
  description = data.get("description", " ")
  
  if title:
    task = Task(title=title,description = description, completed = False)
    db.session.add(task)
    db.session.commit()
    return jsonify({"message": "task criada com sucesso"})
  
  return jsonify({"message": "Erro na criação da task"}), 400


@app.route("/task", methods = ["GET"])
def get_tasks():
  tasks = Task.query.all()
  task_dict = [t.task_to_dict() for t in tasks]
  return jsonify ({"tasks": task_dict, "total_tasks": len(tasks)})

@app.route("/task/<int:id_task>", methods=["DELETE"])
def delete_task(id_task):
  task = Task.query.get(id_task)

  if task:
    db.session.delete(task)
    db.session.commit()
    return jsonify({"message": "task deletada com sucesso"})
  
  return jsonify({"message": "task não encontrada"}), 404 


# isso é para verificar se esta sendo rodado de forma manual
if __name__ == "__main__":
    app.run(debug=True)