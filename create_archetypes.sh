# dg-example-default -> create from scratch
cd dg-example-default
mvn archetype:create-from-project
# cd ../dg-archetypes/dg-example-default
rm -r ../dg-archetypes/dg-example-default/*
cp -fr target/generated-sources/archetype/* ../dg-archetypes/dg-example-default 
cd ..

#TODO: verify that all convertion is done correct!!!



# dg-example-hadoop -> create from scratch
cd dg-example-hadoop
mvn archetype:create-from-project

rm -r ../dg-archetypes/dg-example-hadoop/*
cp -fr target/generated-sources/archetype/* ../dg-archetypes/dg-example-hadoop
cd ..
#TODO: verify that all convertion is done correct!!!
