gst-launch-1.0 -v v4l2src device=/dev/video0 io-mode=dmabuf ! image/jpeg,width=1280,height=720,framerate=30/1 ! rtpjpegpay  ! udpsink host=192.168.7.1 port=5000
