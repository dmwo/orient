gst-launch-1.0 -v videotestsrc ! x264enc tune=zerolatency bitrate=3000 speed-preset=superfast ! rtph264pay ! udpsink host=127.0.0.1 port=5000
