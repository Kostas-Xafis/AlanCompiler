find ./test_files/ -name *.alan | xargs -I @ sh -c 'echo "Testing" @ ; java -jar target/compiler-0.0.2.jar -f @ -t ;'

