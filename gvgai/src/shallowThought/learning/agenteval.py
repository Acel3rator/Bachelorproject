import numpy
import re
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import os

class AgentEval:
    
    def __init__(self):
        self.data = dict()
        self.bots = ["YOLOBOT", "YBCriber", "TUDarmstadtTeam2", "thorbjrn", "SJA862",
        		"Return42", "psuko", "NovTea", "MH2015", "alxio"]


    def evaluate(self):
        # for example: for each game and level find the best bot
	#data = readData()
	# for each game and level save the bot that performed best in games[game][level]
        games = {}
        for i in range(10):  # create empty dict struct 
            games[i] = {}
            for j in range(5):
                games[i][j] = [None, -10000] # [bot, score]
	# go through bots and find best run
        for bot in self.data:
            for game in self.data[bot]:
                for level in self.data[bot][game]:
                    # find best run
                    maximum = -10000
                    for run in self.data[bot][game][level]:
                        try:
                            if self.data[bot][game][level][run][2] > maximum:
                                maximum = self.data[bot][game][level][run][2]
                        except:
                            pass
                    # if best run is better than any other bots...
                    print(game,level)
                    if maximum > games[game][level][1]:
                        games[game][level][0] = bot
                        games[game][level][1] = maximum
        for n in games:
            print(games[n])
		
	
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
        filenames = []
        for file in os.listdir(os.getcwd()+"/."):
            if file.endswith(".txt") and file != "exercises.txt" and file != "solutions.txt" and file != "learning.txt" and file != "README.txt":
                filenames.append(file)
        
        # create data structure
        #{bot: {game: {level: {#ofRepitition: [win, [score], finalScore]}}}}
        for name in filenames:
            rx = re.split('_|\.', name)
            # turn strings from re to int
            for d in range(len(rx)):
                if d not in [0, 1, 3, 6]: # those actually arent numbers
                    rx[d] = int(rx[d])
                # add bot, game, level, run. initialize all dicts:
            if rx[0] not in self.data:
                self.data[rx[0]] = {}
            if rx[2] not in self.data[rx[0]]:
                self.data[rx[0]][rx[2]] = {}
            if rx[4] not in self.data[rx[0]][rx[2]]:
                self.data[rx[0]][rx[2]][rx[4]] = {}
            if rx[5] not in self.data[rx[0]][rx[2]][rx[4]]:
                self.data[rx[0]][rx[2]][rx[4]][rx[5]] = [None, [], None] #[win, [score], final]
            # save stuff
            f = open(name, 'r')
            for line in f:
                line = re.sub('\\n', '', line)
                ry = re.split(',', line)
                if len(ry) == 3:
                    # write score
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][1].append(float(ry[1]))
                if len(ry) == 1 and re.match('didWin: .*', ry[0]):
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][0] = int(re.sub('didWin: ', '', ry[0]))
                if (len(ry) == 1) and (re.match('finalScore: .*', ry[0])):
                    self.data[rx[0]][rx[2]][rx[4]][rx[5]][2] = float(re.sub('finalScore: ', '', ry[0]))
#	return self.data




if __name__ == "__main__":
    ae = AgentEval()
    ae.readData()
    # ae.evaluate()
    ae.plotResults()
