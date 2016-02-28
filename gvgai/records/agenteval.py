import numpy
import re
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import os

class AgentEval:
    
    def __init__(self):
        self.data = dict()
        self.norm_data = dict()
        self.ranks = dict()
        self.bots = ["YOLOBOT", "YBCriber", "TUDarmstadtTeam2", "thorbjrn", "SJA862",
        		"Return42", "psuko", "NovTea", "MH2015", "alxio",
                        "shallowThought"]


#    def evaluate(self):
#        # for example: for each game and level find the best bot
#	#data = readData()
#	# for each game and level save the bot that performed best in games[game][level]
#        games = {}
#        for i in range(10):  # create empty dict struct 
#            games[i] = {}
#            for j in range(5):
#                games[i][j] = [None, -10000] # [bot, score]
#	# go through bots and find best run
#        for bot in self.data:
#            for game in self.data[bot]:
#                for level in self.data[bot][game]:
#                    # find best run
#                    maximum = -10000
#                    for run in self.data[bot][game][level]:
#                        try:
#                            if self.data[bot][game][level][run][2] > maximum:
#                                maximum = self.data[bot][game][level][run][2]
#                        except:
#                            pass
#                    # if best run is better than any other bots...
#                    if maximum > games[game][level][1]:
#                        games[game][level][0] = bot
#                        games[game][level][1] = maximum
#        print("\x1b[1m Best controllers for rum games: \x1b[0m")
#        for n in games:
#            print(games[n])
		
	
    # plot the results
    def plotBotScore(self):
        if not os.path.exists('./plots/scores'):
            os.makedirs('./plots/scores')
        i = 0
        for bot in self.data:
            j = 0
            for game in self.data[bot]:
                k = 0
                for level in self.data[bot][game]:
                    l = 0
                    # TODO: change to aggregation function, for several
                    # repetitions
                    for repetition in self.data[bot][game][level]: 
                        # bot i, game j, level k, rep l, scores
                        try:
                            scores = self.data[i][j][k][self.bots[l]][1]
                            with PdfPages('./plots/scores/' + self.bots[l] + '_' + str(i) + '_' + 
                                    str(j) + '_' + str(k) + '.pdf') as pdf:
                                plt.figure(figsize=(4,4))
                                plt.plot(range(len(scores)), scores)
                                pdf.savefig()
                                plt.close()
                              # plt.show()
                        except:
                            print("data not available for: Game: %d, Level: %d, %s, %d" % (i,
                                j, k, self.bots[l]))
                            pass
                        l += 1
                    k+=1
                j+=1
            i+=1

    
    def plotBotRanking(self):
        """
        1) normalize scores / times
        2) for every time step of the game sort all bots according to their
        score
        3) take every game/level performance of all bots recorded and plot their
        rank at the current point of time in comparison to all the other agents
        """
        print("normalizing Scores")
        self.normalize()
        print("ranking Controllers")
        self.rank()
        if not os.path.exists('./plots/ranks'):
            os.makedirs('./plots/ranks')
        i = 0
        for game in self.ranks:
            j = 0
            for level in self.ranks[game]:
                k = 0
                for run in self.ranks[game][level]:
                    l = 0
                    # TODO: change to aggregation function, for several
                    # repetitions
                    for bot in self.ranks[game][level][run]: 
                        # bot i, game j, level k, rep l, scores
                        try:
                            ranks = self.data[i][j][k][self.bots[l]][1]
                            with PdfPages('./plots/ranks/' + self.bots[l] + '_' + str(i) + '_' + 
                                    str(j) + '_' + str(k) + '.pdf') as pdf:
                                plt.figure(figsize=(4,4))
                                plt.plot(range(len(ranks)), ranks)
                                pdf.savefig()
                                plt.close()
                              # plt.show()
                        except:
                            print("data not available for: Game: %d, Level: %d, %s, %d" % (i,
                                j, k, self.bots[l]))
                            pass
                        l += 1
                    k+=1
                j+=1
            i+=1
  

        

    def normalize(self):
        """
        'normalize' the scores. I.e. extend score records for all bot scores to 2000, so that
        we can compare the scores in a unified way.
        Also add a score of 1000 as soon as the bot hat won to distinguish
        between won and ongoing/lost
        """ 
        for game in self.norm_data:
            for level in self.norm_data[game]:
                for run in self.norm_data[game][level]:
                    for bot in self.norm_data[game][level][run]:
                        scores = self.norm_data[game][level][run][bot]
                        # if not 2000 records, append by value 1000 if won, 
                        # or 0 if lost
                        if len(scores[1]) < 2000:
                             scores[1].append(scores[0] * 1000 + scores[1][-1]) 
                             # and append to longest number of timesteps of any
                             # bot for now (2000):
                             if len(scores[1]) < 2000:
                                 scores[1].extend([scores[1][-1]] 
                                     * (2000 - len(scores[1])))
                        # else add 1000 to last score if won at this exact point
                        else:
                            scores[1][-1] += scores[0] * 1000

    def rank(self):
        """
        Take all bots for each game / level and rank them for each repetition
        of the game
        Ranking is done by taking the score of all ivovled bots and sorting
        them and then replacing score by rank
        """
        self.ranks = self.norm_data
        for game in self.ranks:
            for level in self.ranks[game]:
                for run in self.ranks[game][level]:
                    #take score of all bots and rank 2000 times
                    for i in range(2000):
                        scores = []
                        for bot in self.ranks[game][level][run]:
                            current = self.ranks[game][level][run][bot]
                            #store score + bot name
                            scores.append((current[1][i], bot))
                        # rank.
                        scores = sorted(scores, key=lambda bot: bot[0],
                                        reverse = True)
                        # replace scores with rank
                        for bot in self.ranks[game][level][run]:
                            current = self.ranks[game][level][run][bot]
                            for el in scores:
                                if el[1] == bot:
                                    current[1][i] = scores.index(el) + 1 
        # print(self.ranks[0][0][0]['shallowThought'])
        # print(self.ranks[0][0][0]['YOLOBOT'])

    def readData(self):
        """regExBots = "(?:" + ")|(?:".join(bots) + ")"
        regExFull = re.compile(regExBots + '_game_%d_level_%d_%d.txt')
        """
        # Check for available files
        filenames = []
        for  log in os.listdir(os.getcwd()+"/raw_data_old"):
            if log.endswith(".txt"):
                filenames.append(log)
        
        # create data structure
        #{bot: {game: {level: {#ofRepitition: [win, [score], finalScore]}}}}
        # !!!!!!!!!!!!!!!! CHANGE: 0 -> 2, 2 -> 4, 4 -> 5, 5-> 0 !!!!!!!!!!!!!!!!!
        #{game: {level: {bot: {#ofRepitition: [win, [score], finalScore]}}}}
        for name in filenames:
            rx = re.split('_|\.', name)
            # turn strings from re to int
            for d in range(len(rx)):
                if d not in [0, 1, 3, 6]: # those actually arent numbers
                    rx[d] = int(rx[d])
            # add bot, game, level, run. initialize all dicts:
            if rx[2] not in self.data:
                self.data[rx[2]] = {}
            if rx[4] not in self.data[rx[2]]:
                self.data[rx[2]][rx[4]] = {}
            if rx[5] not in self.data[rx[2]][rx[4]]:
                self.data[rx[2]][rx[4]][rx[5]] = {}
            if rx[0] not in self.data[rx[2]][rx[4]][rx[5]]:
                self.data[rx[2]][rx[4]][rx[5]][rx[0]] = [None, [], None] #[win, [score], final]
            # save stuff
            f = open("./raw_data/" + name, 'r')
            for line in f:
                line = re.sub('\\n', '', line)
                ry = re.split(',', line)
                scores = self.data[rx[2]][rx[4]][rx[5]][rx[0]]
                if len(ry) == 3:
                    # write score
                    scores[1].append(float(ry[1]))
                if len(ry) == 1 and re.match('didWin: .*', ry[0]):
                    scores[0] = int(re.sub('didWin: ', '', ry[0]))
                if (len(ry) == 1) and (re.match('finalScore: .*', ry[0])):
                    scores[2] = float(re.sub('finalScore: ', '', ry[0]))
        self.norm_data = self.data

# TODO: scale rank plots better
# TODO: Add info, after which step the bot has won
# TODO: take several runs of 1 bot into account

if __name__ == "__main__":
    ae = AgentEval()
    print("reading Data")
    ae.readData()
#    ae.evaluate()
    print("Plotting scores")
    ae.plotBotScore()
    print("Plotting ranks")
    ae.plotBotRanking()
