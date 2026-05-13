# AI Voice Keyboard

An Android keyboard that uses your voice and AI to type for you.

## What It Does

- Press the mic button, speak naturally
- AI transcribes your voice and processes the text (grammar correction, tone adjustment, etc.)
- The processed text is inserted into any text field

## Features

- **Voice-to-Text** — Offline STT powered by Sherpa-ONNX
- **AI Text Processing** — Grammar correction, formal/casual tone modes via OpenRouter (free models)
- **Custom Modes** — Create your own processing prompts (e.g., "translate to Indonesian")
- **App Triggers** — Automatically switch modes per app (e.g., formal in Gmail, casual in WhatsApp)
- **Double-Tap Revert** — Revert AI-processed text back to your original words

## Setup

### Prerequisites
- Android Studio ( Hedgehog or newer)
- JDK 17
- An OpenRouter API key (free at [openrouter.ai](https://openrouter.ai))

### Build Locally

1. Clone this repo
2. Run the dependency download script:
   ```bash
   ./download-deps.sh
   ```
3. Open in Android Studio and build

### Get APK from GitHub

1. Go to the **Actions** tab
2. Wait for the build to complete (green checkmark)
3. Scroll down and download `AI-Voice-Keyboard-App`
4. Install the APK on your phone

### After Installing

1. Go to **Settings → System → Languages & Input → On-screen keyboard**
2. Enable **AI Voice Keyboard**
3. Open any app with a text field
4. Switch your keyboard to **AI Voice Keyboard**
5. Open the keyboard settings and enter your OpenRouter API key
6. Download the speech model
7. Start talking!

## Tech Stack

- **Kotlin** + **Jetpack Compose**
- **Dagger Hilt** for dependency injection
- **Room** for local database
- **Retrofit** + **Moshi** for API calls
- **Sherpa-ONNX** for offline speech recognition
- **OpenRouter** for AI text processing (free LLM models)

## License

MIT
