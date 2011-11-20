@echo off
python %~dp0playgame.py -So -e --engine_seed 42 --player_seed 42 --end_wait=0.15 --log_dir game_logs --turns 1000 --turntime 500 --map_file %* | java -jar visualizer.jar

