echo "Sanitizing work environment"
del *.json
del *.txt
del *.class

echo "Generate File to Encrypt"
echo "If you can read this, it worked!!!" > encrypt_me.txt

echo "Compiling Java application"
javac -cp "gson-2.8.2.jar" G.java

echo "running java application"
java -cp ".;gson-2.8.2.jar" G