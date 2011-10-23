@echo off
python %~dp0playgame.py -So --engine_seed 42 --player_seed 42 --end_wait=0.15 --verbose --log_dir game_logs --turns 1000 --turntime 250 --map_file %*  | java -jar visualizer.jar

