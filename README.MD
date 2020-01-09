# Fun Radio factures

Envoyez périodiquement, et facilement, vos factures à **Fun Radio** pour augmenter vos chances.

## Contexte

L'idée de base étant celle-ci, ce serveur peut aussi servir à d'autres usages, pour des tâches redondantes 🤓

## Variables d'environnement (obligatoires)

### Base de données MongoDB

- `MONGO_USERNAME`: Nom d'utilisateur pour accéder à la base de données
- `MONGO_PASSWORD`: Password de l'utilisateur
- `MONGO_HOSTNAME`: Adresse du serveur de la base de données (`localhost` si le serveur n'est pas dans un container, sinon il faut utiliser le nom du container par défaut `mongo_container`)
- `MONGO_PORT`: Port de la base de donnés (défaut: 27017)

### Configuration SMTP

- `SMTP_SERVER`: Adresse du server SMTP (ex: smtp.gmail.com)
- `SMTP_PORT`: Port du serveur SMTP (default: 587)
- `SMTP_USER`: Nom d'utilisateur
- `SMTP_PASSWORD`: Mot de passe de connexion

Un script est disponible pour utiliser utiliser les variables d'environnement. Il sera remplacé à terme par un fichier `.env` plus général pour le _docker-compose_ et le serveur en lui-même.
