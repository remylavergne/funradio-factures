# Fun Radio Factures (periodic mail scheduling)

Envoyez périodiquement, et facilement, vos factures à **Fun Radio** pour augmenter vos chances.

## 🚀🚀 Contexte

OUI, des solutions d'envoi de mails récurrents existent déjà (quelques exemples, pour la plupart payantes):

- [Boomerang for Gmail](https://www.boomeranggmail.com/l/how-to-send-recurring-emails.html)
- [rightinbox](https://www.rightinbox.com/features/recurring-emails)
- ...

MAIS, mon but ici est d'apprendre des choses, et donc, monter en compétences:

- Apprendre le framework [Ktor](https://ktor.io/), et les paradigmes côté serveurs / frontend
- Améliorer mes connaissances en **Kotlin**
- Monter en compétence sur Docker, en _dockerizant_ une solution complète, et la rendre facilement déployable en quelques clics
- Ne pas limiter l'envoi de mail à un seul type de service, et avoir une configuration souple
- Prendre du plaisir ❤️

## 🌍 Variables d'environnement (obligatoires)

### Base de données MongoDB

- `MONGO_USERNAME`: Nom d'utilisateur pour accéder à la base de données
- `MONGO_PASSWORD`: Password de l'utilisateur
- `MONGO_HOSTNAME`: Adresse du serveur de la base de données (`localhost` si le serveur n'est pas dans un container, sinon il faut utiliser le nom du container par défaut `mongo_container`)
- `MONGO_PORT`: Port de la base de donnés (défaut: 27017)

## 🏁 Lancer l'application (JAR seul pour le moment)

- Télécharger la dernière release stable (JAR) : [<https://github.com/remylavergne/funradio-factures/releases>](Dernière release)
- Exécuter le JAR :

```terminal
$ docker-compose up -d
// Lance la base de données MongoDB
$ java -jar factures-{version}-all.jar
// Lance le serveur
```

## 👩‍💻 Comment utiliser l'application

Cette application peut s'utiliser via des appels REST ([REST Client pour VS Code](https://github.com/Huachao/vscode-restclient/releases))

Une interface web est en développement pour faciliter son utilisation.

### Créer un nouvel email (informations obligatoires)

```http
POST /create HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data; boundary=--------------------------011705091029995234177780

----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="receiverEmail"

example@gmail.com
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="mailTitle"

title
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="mailBody"

body
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="repeatEvery"

10000
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="delayMillis"

10000
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="receiverName"

Rémy Lavergne
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="senderName"

Spotfinder
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="senderEmail"

example@gmail.com
----WebKitFormBoundary7MA4YWxkTrZu0gW

```

```http
Email with id f4528697-9a71-468a-9e13-9a7abd2d36b7 has been created.
```

### Création d'un serveur SMTP

```http
POST /smtp HTTP/1.1
Host: localhost:8080

{
    "server": "smtp.gmail.com",
    "port": 587,
    "login": "mon.login",
    "password": "M0n_paSsWord"
}
```

### ⚠️ Ajouter un serveur SMTP pour pouvoir envoyer l'email

```http
POST /smtp/link HTTP/1.1
Host: localhost:8080

{
    "emailId": "f4528697-9a71-468a-9e13-9a7abd2d36b7",
    "smtpServerId": "814ac352-16be-4684-bc85-cbf0f0bfc18b"
}
```

### Lancer l'envoi automatique de cet email

```http
POST http://localhost:8080/scheduling
Content-Type: application/json

{
    "start": true,
    "emailId": "f4528697-9a71-468a-9e13-9a7abd2d36b7"
}
```

### Arrêter l'envoi automatique de cet email

```http
POST http://localhost:8080/scheduling
Content-Type: application/json

{
    "start": false,
    "emailId": "f4528697-9a71-468a-9e13-9a7abd2d36b7"
}
```

### Voir tous les mails actifs

```http
GET /scheduling HTTP/1.1
Host: localhost:8080
```

Réponse:

```json
[
  {
    "id": "5fb91ec6-a922-422c-840c-709958aa2b85",
    "receiver": {
      "id": "2da5a8ad-9228-4586-bc9c-57839d78140d",
      "name": "Rémy Lavergne",
      "email": "example@gmail.com"
    },
    "sender": {
      "id": "7ca785de-d8f3-430b-9243-08feb8d507a4",
      "name": "Spotfinder",
      "email": "example@gmail.com"
    },
    "title": "title",
    "body": "body",
    "createdAt": 1579292547631,
    "active": true,
    "delayMillis": 10000,
    "repeatEvery": 10000
  }
]
```
