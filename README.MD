# Fun Radio factures (periodic mail scheduling)

Envoyez périodiquement, et facilement, vos factures à **Fun Radio** pour augmenter vos chances.

## Contexte

OUI, des solutions d'envoi de mails récurrants existent déjà (quelques exemples, pour la plupart payantes):

- [Boomerang for Gmail](https://www.boomeranggmail.com/l/how-to-send-recurring-emails.html)
- [rightinbox](https://www.rightinbox.com/features/recurring-emails)
- ...

MAIS, mon but ici est d'apprendre des choses, et donc, monter en compétences:

- Apprendre le framework [Ktor](https://ktor.io/), et les paradigmes côté serveurs / frontend
- Améliorer mes connaissances en **Kotlin**
- Monter en compétence sur Docker, en *dockerizant* une solution complète, et la rendre facilement déployable en quelques clics
- Ne pas limiter l'envoi de mail à un seul type de service, et avoir une configuration souple
- Prendre du plaisir ❤️

## 🌍Variables d'environnement (obligatoires)

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
