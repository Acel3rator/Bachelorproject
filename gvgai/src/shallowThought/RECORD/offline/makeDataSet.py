import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import os
import random
import re

class AgentEval:
    
    def __init__(self):
        self.data = dict()
        self.bots = ["YOLOBOT", "YBCriber", "TUDarmstadtTeam2", "thorbjrn", "SJA862",
                "Return42", "psuko", "NovTea", "MH2015", "alxio"]
        gameNames = []
        gameNames += ["aliens", "boulderdash", "butterflies", "chase", "frogs", "missilecommand", "portals", "sokoban", "survivezombies", "zelda"]
        gameNames += ["camelRace", "digdug", "firestorms", "infection", "firecaster", "overload", "pacman", "seaquest", "whackamole", "eggomania"]
        gameNames += ["bait", "boloadventures", "brainman", "chipschallenge",  "modality", "painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle"]
        gameNames += ["roguelike", "surround", "catapults", "plants", "plaqueattack", "jaws", "labyrinth", "boulderchase", "escape", "lemmings"]
        gameNames += ["solarfox", "defender", "enemycitadel", "crossfire", "lasers", "sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky"]
        gameNames += ["lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "racebet2", "intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"]
        self.games = gameNames

    def makeTrainingDataSet(self):

        gameNames = self.games[:]

	    # Split into training/validation/test
        train, validation, test = [], [], []
        while (len(train) < 40):
            level = random.choice(gameNames)
            train.append(level)
            gameNames.remove(level)
        while (len(validation) < 10):
            level = random.choice(gameNames)
            validation.append(level)
            gameNames.remove(level)
        while (len(test) < 10):
            level = random.choice(gameNames)
            test.append(level)
            gameNames.remove(level)

        self.readData()

        i = 0
        for part in [train, validation, test]:
            if i == 0:
                f = open("train.txt", "w")
            elif i == 1:
                f = open("validation.txt", "w")
            else:
                f = open("test.txt", "w")
            for game in part:
                for level in range(5):
                    try:                    
                        # critical part (right now just use env in beginning of game):
                        #self.data[bot][game][level][run][2]
                        
                        env = self.data[self.bots[0]][self.games.index(game)][level][0][1][0][1] # i feel bad :(
                    except (KeyError, IndexError) as e:
                        print("No data for {}, error in: game={}({}), level={}, run={}".format(e, game, self.games.index(game), level, "not defined"))
                    else:
                        botWon = []
                        for bot in self.bots:
                            try:
                                if self.data[bot][self.games.index(game)][level][0][0]: #only check first run..
                                    botWon.append(1)
                                else:
                                    botWon.append(0)
                            except:
                                botWon.append(0)  # if no data for bot, assume we dont wanna take it
                        # now we have the env array as input, the botWon array as output
                        env.extend(botWon)
                        line = [str(l) for l in env]
                        line = ",".join(line)
                        f.write(line + "\n")
                    
            f.close()
            i+=1  # next set
	
    # plot the results
    def plotResults(self):
        if not os.path.exists('./plots'):
            os.makedirs('./plots')
        i = 0
        for bot in self.data:
            j = 0
            for game in self.data[bot]:
                k = 0
                for level in self.data[bot][game]:
                    l = 0
                    # change to aggregation function, for several
                    # repetitions
                    for repetition in self.data[bot][game][level]: 
                        # bot i, game j, level k, rep l, scores
                        try:
                            scores = self.data[self.bots[i]][j][k][l][1]
                            with PdfPages('./plots/' + self.bots[i] + '_' + str(j) + '_' + 
                                    str(k) + '_' + str(l) + '.pdf') as pdf:
                                plt.figure(figsize=(4,4))
                                plt.plot(range(len(scores)), scores)
                                pdf.savefig()
                                plt.close()
                              # plt.show()
                        except:
                            pass
                        l += 1
                    k+=1
                j+=1
            i+=1


    def readData(self):
        """regExBots = "(?:" + ")|(?:".join(bots) + ")"
        regExFull = re.compile(regExBots + '_game_%d_level_%d_%d.txt')"""
        # Check for available files
        filenamesACT = []
        filenamesENV = []
        for file in os.listdir(os.getcwd()+"/."):
            if file.endswith(".txt") and file != "features.txt" and file != "README.txt":
                if file.endswith("actions.txt") and os.stat(file).st_size > 0:
                    filenamesACT.append(file)
                if file.endswith("env.txt") and os.stat(file).st_size > 0:
                    filenamesENV.append(file)
        
        # create data structure
        # {bot: {game: {level: {#ofRepitition: [win, [(score, [ENV-FEATURES])], finalScore]}}}}
        for name in filenamesACT:
            rx = re.split('_|\.', name)
            # turn strings from re to int
            for d in range(len(rx)):
                if d not in [0, 1, 3, 6, 7]: # those actually arent numbers
                    rx[d] = int(rx[d])
                # add bot, game, level, run. initialize all dicts:
            if rx[0] not in self.data:
                self.data[rx[0]] = {}           # create bot-entry
            if rx[2] not in self.data[rx[0]]:
                self.data[rx[0]][rx[2]] = {}    # create game-entry
            if rx[4] not in self.data[rx[0]][rx[2]]:
                self.data[rx[0]][rx[2]][rx[4]] = {}     # create level
            if rx[5] not in self.data[rx[0]][rx[2]][rx[4]]:
                self.data[rx[0]][rx[2]][rx[4]][rx[5]] = [None, [], None] #[win, [(score, [env])], final]
            # save stuff
            f = open(name, 'r')
            env = self.extractFeat(name)
            for line in f:
                line = re.sub('\\n', '', line)
                ry = re.split(',', line)
                if len(ry) == 3:
                    # write score and env features
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][1].append((float(ry[1]), env[0]))
                if len(ry) == 1 and re.match('didWin: .*', ry[0]):
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][0] = int(re.sub('didWin: ', '', ry[0]))
                if (len(ry) == 1) and (re.match('finalScore: .*', ry[0])):
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][2] = float(re.sub('finalScore: ', '', ry[0]))
                    # {bot: {game: {level: {#ofRepitition: [win, [score], finalScore]}}}}

    def extractFeat(self, f):
        """
        Gets actions-file-name and extracts environment information from
        the corresponding env-file
        """
        f = f[:-11] + "env.txt"  # -11 for slicing away "actions.txt"
        
        # HERE WE CHOOSE WHICH FEATURES TO USE! (full list and order in features.txt)
        fp = open(f)
        lines = fp.readlines()
        lines = [l.rstrip("\n") for l in lines]
        lines = [l.split(',') for l in lines]
        for l in range(len(lines)):
            for m in range(len(lines[l])):
                lines[l][m] = self.processFeature(lines[l][m]) ## does this even work?!
        # we try: #npcs, #immov, # mov, #res, #portals
        fp.close()
        processedFeatures = []
        for l in lines:
            # THIS IS ONLY THE NUMBER OF DIFFERENT OBJECTS OF EACH TYPE!!!
            processedFeatures.append([len(l[5]), len(l[6]), len(l[7]), len(l[8]), len(l[9])])
        return processedFeatures

                
    def processFeature(self, feat):
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
    ae = AgentEval()
    ae.makeTrainingDataSet()
