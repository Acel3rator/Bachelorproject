exercises is arranged as follows:

the 5 main arguments are seperated by ":"
example:
OLMCTSAgent:catapults:1:20/20:MCTS_ITERATIONS=100:NUM_TURNS=1to20&ROLLOUT_DEPTH=1to25
AgentName:GameName:LevelIndex:numberOfGames:fixedParameters:optimizeParameters

where:
numberOfGames = howManyAreLeft/howManyInTotal
fixedParameters = param1=value1&param2=value2&...&paramN=valueN
optimizeParameters = param1=AtoB&param2=CtoD&...&paramN=YtoZ   // just be sure to assign each parameter the correct values (ints, doubles...)