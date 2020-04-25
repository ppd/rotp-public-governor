find . -name "*.jpg" | xargs -n1 -P10 ./jpg2webp.sh
find . -name "*.png" | xargs -n1 -P10 ./png2webp.sh

find . -name "*.java" -exec sed -i.bak s/png/webp/ {} \;
find . -name "*.java" -exec sed -i.bak s/jpg/webp/ {} \;

find . -name "*.txt" -exec sed -i.bak s/png/webp/ {} \;
find . -name "*.txt" -exec sed -i.bak s/jpg/webp/ {} \;



