import re
import os

class CreateTS:

    def __init__(self):
        self.data = dict()
        self.ranks = dict()
        self.bots = ["YOLOBOT", "YBCriber", "TUDarmstadtTeam2", "SJA862",
                     "NovTea", "MH2015", "alxio",] # olmcts, ra, BinarySearch...
        

    def readData(self):
        """regExBots = "(?:" + ")|(?:".join(bots) + ")"
        regExFull = re.compile(regExBots + '_game_%d_level_%d_%d.txt')
        """
        # Check for available files
        filenames = []
        for log in os.listdir(os.getcwd()+"/raw_data"):
            if log.endswith(".txt"):
                filenames.append(log)
        
        # create data structure
        #{game: {level: {bot: {#ofRepitition: [win, [score], finalScore]}}}}
        for name in filenames:
            rx = re.split('_|\.', name)
            # turn strings from re to int
            for d in range(len(rx)):
                if d not in [0, 1, 3, 6]: # those actually arent numbers
                    rx[d] = int(rx[d])
            # add  game, level, bot, run. initialize all dicts:
            if rx[2] not in self.data:
                self.data[rx[2]] = {}
            if rx[4] not in self.data[rx[2]]:
                self.data[rx[2]][rx[4]] = {}
            if rx[0] not in self.data[rx[2]][rx[4]]:
                self.data[rx[2]][rx[4]][rx[0]] = [[], []] #[(wins), (final scores)]
            # save stuff
            f = open("./raw_data/" + name, 'r')
            for line in f:
                line = re.sub('\\n', '', line)
                ry = re.split(',', line)
                scores = self.data[rx[2]][rx[4]][rx[0]]
                if len(ry) == 1 and re.match('didWin: .*', ry[0]):
                    scores[0].append(int(re.sub('didWin: ', '', ry[0])))
                if (len(ry) == 1) and (re.match('finalScore: .*', ry[0])):
                    scores[1].append(float(re.sub('finalScore: ', '', ry[0])))


    def determine_winner(self):
        """
        Take all bots for each game / level and rank them according to the score
        Ranking is done by taking the score of all ivovled bots and sorting
        them and adding 10k, if won
        """
        f = open('../src/shallowThought/Best_Agents.txt', 'w+')
        for game in self.data:
            for level in self.data[game]:
                scores = []
                for bot in self.data[game][level]:
                    current = self.data[game][level][bot]
                    for i, score in enumerate(current[1]):
                        if current[0][i] == 1:
                            #store score + large number to weight winning
                            scores.append((bot, current[1][i] + 10000,
                                current[0][i]))
                        else:
                            scores.append((bot, current[1][i], current[0][i]))
                # rank & write to result file.
                scores = sorted(scores, key=lambda bot: bot[1],
                                reverse = True)
                print(scores)
                # line = game, level, best bot, score, didwin
                f.write(str(game) + ', ' + str(level) + ', ' + scores[0][0] +
                        ',\t' + str(scores[0][1]) + ',\t' +
                        str(scores[0][2]) + '\n')
        f.close()


        

    
# TODO: Add info, after which step the bot has won

if __name__ == "__main__":
    cts = CreateTS()
    print("reading Data")
    cts.readData()
    print("finding best agent for each game")
    cts.determine_winner()
   # print("creating trainig set file")
   # cts.createTS()
    print("Done. Saved at: ../src/shallowThought/Best_Agents.txt")
