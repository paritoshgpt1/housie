# 🎯 Housie Ticket Generator and Playing Platform

A complete digital platform for playing Housie (Tambola/Bingo) games with separate interfaces for organizers and players. Built with Spring Boot and designed for both local and cloud deployment.

## 🎮 What is Housie?

Housie is a popular number-based game where players mark numbers on their tickets as they are called out. The first player to complete specific patterns (like Full House, Early 7, etc.) wins prizes.

## ✨ Features

### 🎯 For Organizers
- **Game Setup**: Create game sessions with secure access codes
- **Player Management**: Add players with customizable ticket counts (1-6 tickets per player)
- **Interactive Game Board**: 
  - Random number generation (1-90) with configurable speed
  - Real-time number display with voice announcements
  - Claim validation system for different winning patterns
- **Dividend Management**: Define and manage various winning patterns
- **Secure Access**: Organizer-only features with authentication

### 🎫 For Players
- **Ticket Generation**: Automatic generation of valid Housie tickets
- **Secret Code Access**: Secure ticket access using unique codes
- **Game Participation**: View tickets and play along with the game
- **Claim Submission**: Submit claims for different winning patterns

### 🏆 Winning Patterns (Dividends)
- **Full House**: All numbers on the ticket
- **Early 7**: First 7 numbers
- **Top Line**: First row
- **Middle Line**: Second row  
- **Bottom Line**: Third row
- **Four Corners**: Corner numbers
- **Breakfast, Lunch, Dinner**: Specific number combinations
- **And many more...**

## 🛠️ Technology Stack

- **Backend**: Spring Boot 2.2.6
- **Database**: PostgreSQL
- **Frontend**: Thymeleaf + Bootstrap CSS
- **Build Tool**: Maven
- **Deployment**: Heroku-ready
- **Java Version**: 1.8

## 🚀 Getting Started

### Prerequisites
- Java 17 runtime (for Docker image) or Java 8+ locally
- Maven 3.8+
- PostgreSQL database (local or Neon)

### Run Locally (Maven)

1) Clone
   ```bash
   git clone <repository-url>
   cd Housie
   ```

2) Set env vars (terminal session)
   ```bash
   export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/housie?sslmode=disable"
   export SPRING_DATASOURCE_USERNAME="your_username"
   export SPRING_DATASOURCE_PASSWORD="your_password"
   export SPRING_JPA_HIBERNATE_DDL_AUTO=update
   export CUSTOM_SCHEME=http
   export CUSTOM_HOST=localhost:8080
   ```

3) Run
   ```bash
   mvn spring-boot:run
   ```

4) Open
   - `http://localhost:8080/welcome`
   - `http://localhost:8080/organizer`
   - `http://localhost:8080/game-board`

Notes
- `server.port` honors `PORT` if set; defaults to 8080.
- The app reads datasource username/password from env; you can also embed them in the JDBC URL.

### Run Locally (Docker)

```bash
docker build -t housie:local .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/housie?sslmode=disable" \
  -e SPRING_DATASOURCE_USERNAME="your_username" \
  -e SPRING_DATASOURCE_PASSWORD="your_password" \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -e CUSTOM_SCHEME=http \
  -e CUSTOM_HOST=localhost:8080 \
  housie:local
```

Open `http://localhost:8080/welcome`.

## ☁️ Deploy: Neon + Render

This repo includes a Dockerfile and `render.yaml` blueprint for Render.

### Neon (Postgres) Setup
- Create a Neon project and database.
- Get the connection string (psql form) and map it to Spring env vars:
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<db>?sslmode=require`
  - `SPRING_DATASOURCE_USERNAME=<user>`
  - `SPRING_DATASOURCE_PASSWORD=<password>`

Example (from a typical Neon URL):
```text
jdbc:postgresql://ep-calm-scene-ad0926ar-pooler.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require
username=neondb_owner
password=... (your Neon password)
```

### Render Service Setup
- Create a Web Service (Docker) from this repo/branch.
- Health check path: `/welcome` (already set in render.yaml).
- Set these Environment variables in Render:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
  - `CUSTOM_SCHEME=https`
  - `CUSTOM_HOST=<your-service>.onrender.com`
- Leave `PORT` unset (Render injects it).

### Render CLI (optional)
- Install via Homebrew: `brew install render`.
- Login: `render login` or set `RENDER_API_KEY`.
- List services: `render services list --output text`.
- Trigger deploy: `render deploys create <serviceId> --confirm`.
- Tail logs: `render logs -r <serviceId> --type runtime --output text --limit 200`.

### Troubleshooting
- `UnknownHostException` for Neon: remove quotes/newlines from `SPRING_DATASOURCE_URL` and ensure `sslmode=require`.
- Startup fails on Render with `${PORT}`: Dockerfile uses `sh -c` to expand `$PORT` and passes it to webapp-runner; ensure you’re on latest.
- Health failing: verify `/welcome` returns 200 and DB env vars are set.

## 🎯 How to Play

### For Organizers

1. **Access Organizer Panel**
   - Go to `/organizer`
   - Enter your organizer code (contact admin for access)

2. **Create Game Session**
   - Enter player count and details
   - Generate unique codes for each player
   - Share player codes with participants

3. **Start the Game**
   - Go to `/game-board`
   - Use the number generator to call numbers
   - Validate player claims using the validation system

### For Players

1. **Get Your Tickets**
   - Go to `/welcome`
   - Enter your secret code
   - View your generated tickets

2. **Play the Game**
   - Mark numbers on your tickets as they are called
   - Submit claims when you complete winning patterns

## 📁 Project Structure

```
src/
├── main/
│   ├── java/io/github/paritoshgpt1/Housie/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── util/            # Utility classes
│   │   └── wrapper/         # Service wrappers
│   └── resources/
│       ├── static/          # CSS, JS, images
│       ├── templates/       # Thymeleaf templates
│       └── application*.properties
└── test/                     # Test classes
```

## 🎨 Key Components

### Models
- **Player**: Player information and ticket count
- **Ticket**: Individual Housie tickets with numbers
- **Organizer**: Game organizer details
- **Round**: Game rounds and called numbers
- **Dividend**: Winning patterns and descriptions
- **Claim**: Player claims for winning patterns

### Controllers
- **OrganizerController**: Handles organizer operations
- **TicketController**: Manages ticket generation
- **GameBoardController**: Controls game board functionality
- **RoundController**: Manages game rounds

### Utilities
- **Tambola**: Core ticket generation algorithm
- **Constants**: Application constants
- **CustomConfig**: Configuration management

## 🔧 Configuration

### Database Configuration
The application uses environment variables for datasource configuration:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO` (e.g., `update`)
- `CUSTOM_SCHEME` and `CUSTOM_HOST` for generating ticket links

### Custom Properties
```properties
custom.scheme=http/https
custom.host=your-domain.com
```

## 🎵 Features

- **Voice Announcements**: Number calling with text-to-speech
- **Responsive Design**: Works on desktop and mobile
- **Real-time Updates**: Live game board updates
- **Secure Access**: Unique codes for players and organizers
- **Validation System**: Built-in claim validation
- **Visual Dividends**: Image representations of winning patterns

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Paritosh** - [@paritoshgpt1](https://github.com/paritoshgpt1)

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the developer for organizer access codes

---

**Happy Playing! 🎉**
