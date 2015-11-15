import numpy
import re
import matplotlib.pyplot as plt
import os

def evaluate():
	# for example: for each game and level find the best bot
	#1. make dict struct:
	data = readData()
	games = {}
	for i in [0,1,2,3,4,5,6,7,8,9]:
		games[i] = {}
		for j in [0,1,2,3,4]:
			games[i][j] = None
	for bot in data:
		for game in data[bot]:
			for level in data[bot][game]:
				maximum = -10000
				for run in data[bot][game][level]:
					if data[bot][game][level][run]
		scores = []
		
	
"""
	# plot
	l = []
	for i in range(1, len(d)):
		l.append(float(d[str(i)]))
	plt.plot(l)	
	plt.show()
"""

def readData():
	bots = ["YOLOBOT", "YBCriber", "TUDarmstadtTeam2", "thorbjrn", "SJA862",
        		"Return42", "psuko", "NovTea", "MH2015", "alxio"]
	"""regExBots = "(?:" + ")|(?:".join(bots) + ")"
	regExFull = re.compile(regExBots + '_game_%d_level_%d_%d.txt')"""
	# Check for available files
	filenames = []
	for file in os.listdir(os.getcwd()+"/."):
    		if file.endswith(".txt"):
        		filenames.append(file)

	# create data structure
	data = {}
	#[bot, [game, [level, [#ofRepitition, (boolWin, [score], finalScore)]]]]
	for name in filenames:
		rx = re.split('_|\.', name)
		# add bot, game, level, etx. initialize all dicts:
		if rx[0] not in data:
			data[rx[0]] = {}
		if rx[2] not in data[rx[0]]:
			data[rx[0]][rx[2]] = {}
		if rx[4] not in data[rx[0]][rx[2]]:
			data[rx[0]][rx[2]][rx[4]] = {}
		if rx[5] not in data[rx[0]][rx[2]][rx[4]]:
			data[rx[0]][rx[2]][rx[4]][rx[5]] = [None, [], None] 
		# save stuff
		f = open(name, 'r')
		for line in f:
			line = re.sub('\\n', '', line)
			ry = re.split(',', line)
			if len(ry) == 3:
				# write score
				data[rx[0]][rx[2]][rx[4]][rx[5]][1].append(ry[1])
			if len(ry) == 1 and re.match('didWin: .*', ry[0]):
				data[rx[0]][rx[2]][rx[4]][rx[5]][0] = re.sub('didWin: ', '', ry[0])
			if (len(ry) == 1) and (re.match('finalScore: .*', ry[0])):
				data[rx[0]][rx[2]][rx[4]][rx[5]][2] = re.sub('finalScore: ', '', ry[0])
	return data


evaluate()
