find . -name "*.wav" | xargs -n1 -P7 oggenc

find . -name "audio.txt" -exec sed -i.bak s/\\.wav/\\.ogg/ {} \;
