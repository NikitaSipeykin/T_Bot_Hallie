# 🤖 T_Bot_Hallie — Full-stack Telegram Bot & Admin Panel

## 📝 Project Overview

**T_Bot_Hallie** is a commercial-grade, modular system designed for delivering educational courses via Telegram. It combines a sophisticated bot for end-users with a web-based administration panel for content management and business analytics.

> This project was built with a focus on **High Availability** and **Observability**, reflecting my background in performance engineering.

---

## 🌟 Key Features

### 🤖 Telegram Bot Capabilities

- **Automated Course Delivery** — Structured flow of lessons, including audio, video circles, and documents.
- **Smart Reminders** — Automatic notifications for users who drop off at specific stages (e.g., after the first day or at the payment block).
- **Integrated Payment System** — Support for both automated and manual payment verification.
- **Personalization** — User progress tracking, name-based addresses, and personalized quiz results.
- **Email Confirmation** — Verification codes sent via email for gift redemption.

### 🖥️ Admin Dashboard (Web App)

- **Dynamic Content Management** — Real-time editing of bot texts and media without code redeployment.
- **Broadcast Engine** — Scheduled mass messaging to all or specific segments of users.
- **User Interaction** — Dedicated interface for admins to view user messages and reply directly.

### 📊 Analytics & Monitoring

- **Business Intelligence** — Tracking churn rates (unsubscribes), payment funnel conversion, and block-by-block user retention.
- **Live Dashboards** — Deep integration with Grafana to visualize system health and business metrics.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Java, Spring Boot (Modular Architecture) |
| **Frontend** | HTML, JavaScript, CSS (Admin Panel) |
| **Database** | PostgreSQL |
| **Containerization** | Docker & Docker Compose |
| **Orchestration** | Kubernetes (k8s) |
| **CI/CD** | GitHub Actions |
| **Monitoring** | Prometheus & Grafana |

---

## 📂 Project Structure

The project is organized into several specialized modules for better maintainability:

```
T_Bot_Hallie/
├── module_core/        # Core business logic and database interactions
├── telegram-bot/       # Telegram API integration and command handling
├── web-app/            # React/HTML based administration interface
├── module_analytics/   # Logic for tracking user events and metrics
├── module_payment/     # Integration with payment gateways
└── k8s/                # Kubernetes configuration files for production deployment
```

---

## 🚀 Implementation & Deployment

The system is designed to be cloud-native and easily deployable.

**1. Clone the repository:**
```bash
git clone https://github.com/your-username/T_Bot_Hallie.git
cd T_Bot_Hallie
```

**2. Local Development** — Use Docker Compose to spin up the bot, web-app, and database locally:
```bash
docker-compose up
```

**3. Production** — Configured for Kubernetes clusters. Deployment manifests are located in the `/k8s` directory:
```bash
kubectl apply -f k8s/
```

---

## 👤 Author & Contacts

**Nikita Sipeikin** — Backend Java Developer & Performance Engineer

[![Email](https://img.shields.io/badge/Email-niksipeikin%40gmail.com-blue?style=flat&logo=gmail)](mailto:niksipeikin@gmail.com) <br/>
[![LinkedIn](https://img.shields.io/badge/LinkedIn-nikita--sipeikin-blue?style=flat&logo=linkedin)](https://linkedin.com/in/nikita-sipeikin) <br/>
[![Portfolio](https://img.shields.io/badge/Portfolio-nikita--sipeikin.vercel.app-black?style=flat&logo=vercel)](https://nikita-sipeikin.vercel.app) <br/>
[![WhatsApp](https://img.shields.io/badge/WhatsApp-%40NikSipeykin-green?style=flat&logo=whatsapp)](https://wa.me/NikSipeykin) <br/>

---

## 📄 License

This is a commercial project repository. **All rights reserved.**
