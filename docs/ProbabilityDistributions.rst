Probability Distributions
=========================

DataTransformers make a convenient hook to adding probabilistic behavior into DataGenerator, either during post processing or during the search over the model.

Choosing Elements from a Set
----------------------------

If one desires to choose elements randomly from a set of values with weighted or uniform probabilities on the elements, any weighted choice code will work. Simply wrap the weighted choice code with a transformer that will set the appropriate variable to the chosen element.

Choosing Numbers from a Distribution
------------------------------------

The same idea extends readily to choosing numbers. Use any code that will generate numbers from your chosen distributions and wrap it with code to convert the result to a String and assign it to the appropriate variable.