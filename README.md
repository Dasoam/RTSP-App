
# RTSP Streaming Android App

An Android application built in **Java** that enables users to stream RTSP feeds, record live video locally, and view the stream in **Picture-in-Picture (PiP)** mode.

---

## 🚀 Features

- 🎥 **Live RTSP Streaming** – Play video from an RTSP stream.
- 💾 **Recording** – Save the streamed video in `.ts` or `.mkv` format locally.
- 📺 **Picture-in-Picture Mode** – Continue watching the stream in a floating window.
- ⏹️ **Stop Streaming** – Gracefully stop the playback and release resources.

---

## 🔧 Technologies Used

- **Java** (Android SDK)
- **LibVLC / VLC Android SDK** – For handling RTSP streaming and media playback.
- **AndroidX** – For PiP and modern UI support.

---

## 📖 About RTSP

**RTSP (Real-Time Streaming Protocol)** is a network control protocol designed for use in entertainment and communications systems to control streaming media servers. RTSP acts as a “remote control” for media servers, providing commands such as play, pause, record, and teardown, while the actual media data is typically transmitted via RTP (Real-Time Transport Protocol)[3]. This protocol is widely used for low-latency streaming from IP cameras and surveillance devices, making it ideal for real-time monitoring applications.

**Common RTSP Commands:**
- `OPTIONS` – Query server capabilities
- `DESCRIBE` – Request description of the stream
- `SETUP` – Negotiate transport parameters
- `PLAY` – Start streaming
- `PAUSE` – Temporarily halt the stream
- `RECORD` – Start recording (if supported)
- `TEARDOWN` – End the session[3]

---

## ⚙️ How It Works

1. **Input RTSP URL:**  
   Enter the RTSP stream address (e.g., `rtsp://username:password@camera_ip:port/stream`) in the app.
2. **Stream Playback:**  
   The app connects to the RTSP server, negotiates the session, and starts real-time playback using the VLC library for robust compatibility.
3. **Recording:**  
   Users can record the live stream locally in `.ts` or `.mkv` format for later viewing or evidence.
4. **Picture-in-Picture:**  
   Switch to PiP mode to keep the stream visible while using other apps.
5. **Stop & Resource Management:**  
   Stopping the stream ensures resources are released, optimizing device performance.

---

## 🛠️ Integration Notes

- **RTSP Support in Android:**  
  Native Android media players have limited RTSP support. Using libraries like VLC or ExoPlayer (with the RTSP extension) is recommended for broader compatibility and better codec support[1][5].
- **Authentication:**  
  RTSP URLs can include credentials for streams that require authentication (e.g., `rtsp://user:pass@host/stream`)[1].
- **Network Compatibility:**  
  RTSP streams may use UDP or TCP for transport. Some networks (especially those behind NAT or firewalls) may require TCP (RTP over RTSP) for reliable playback[1].
- **Codec Support:**  
  Ensure your RTSP stream uses widely supported codecs (e.g., H.264 for video, AAC for audio) for maximum compatibility[1][5].

---

## 📝 Example RTSP URLs

- `rtsp://username:password@192.168.1.100:554/stream1`
- `rtsp://camera.example.com/live.sdp`

---

## 🚦 Limitations & Considerations

- **Codec Limitations:**  
  Only streams using supported codecs (commonly H.264 video, AAC/AC3 audio) will play reliably on all devices[1][5].
- **Network Latency:**  
  RTSP is optimized for low latency but may be affected by network conditions[3][7].
- **Security:**  
  RTSP streams are often unencrypted; use secure networks or VPNs for sensitive feeds[7].

---

## 📚 Further Reading

- [RTSP Protocol Overview – Wowza][3]
- [Android ExoPlayer RTSP Support][1]
- [GStreamer for RTSP on Android][5]

---

**Enjoy seamless RTSP streaming, recording, and multitasking with this app!**
