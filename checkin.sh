git pull
find android/fmradio/app/src/ -name "*.java" |xargs -L1 java -jar ./android/google-java-format-1.5-all-deps.jar --replace
git add --all
git commit -m "automated chkin"
git push origin master
