import re

def printFeatures(file, part="all"):
    """
    Extracts features from file and prints them.
    Values in file are comma-seperated.
    Values are typically one of the following domains:
    1. ints or floats
    2. vector of ints or floats, denoted as 123 : 456
    3. list of vectors, denoted as 123 : 456|789 : 012
    4. list of lists of vectors, denoted like 3., with the
       addition of each sublist commencing with #
    """
    with open("features.txt", 'r') as f:
        features = f.readlines()
        features = [feat.split(',') for feat in features]
    with open(file, 'r') as f:
        print("Printing observations for level X:")
        values = f.readlines()
        # Since the file is a whole game run, here you decide whether all, first or last state
        if part == "first":
            values = [values[0]]
        elif part == "last":
            values = [values[-1]]
        # Split (csv)
        values = [val.split(',') for val in values]
        longestFeature = max([len(feat) for feat in features])  # just for display-niceness
        align = longestFeature + 3
        for feat in features:
            for val in values:
                for f, v in zip(feat, val):    
                    # List of list of stuff:
                    v = processFeature(v)
                    print(f+":", end='')
                    print((align-len(f))*' ', end='')
                    for i in range(len(v)):
                        if i != 0:
                            print("\n", end='')
                            print((1+align)*' ', end ='')
                        for j in range(len(v[i])):
                            if len(v[i][j]) == 1:
                                # it's a sole number
                                print("{}".format(v[i][j][0]), end='')
                            elif len(v[i][j]) == 2:
                                # it's a vector
                                print("{}".format((v[i][j][0], v[i][j][1])), end='')
                            else:
                                print(v[i][j])
                    print("\n", end='')
                 
def processFeature(feat):
    """
    Returns type of feature in string and values in list(s).
    "#10 : 5|3 : 1|2 : 4#5 : 1|2 : 4" -> [[(10,5),(3,1),(2,4)],[(5,1),(2,5)]]
    """
    feat = feat.strip('#')
    feat = feat.split('#')
    feat = [f.split('|') for f in feat]
    feat = [[f.split('+') for f in f2] for f2 in feat]
    return (feat)        
    
if __name__ == "__main__":
    printFeatures("testestest.txt", "last")
