# Makefile for FP8 FPU simulation

# 源文件和目标
SV_SRC = src/main/resources/Fpu.sv
CPP_SRC = src/test/csrc/testacc.cpp
TARGET = fpu_sim

# Verilator配置
VERILATOR = verilator
VERILATOR_FLAGS = --trace-fst --trace --cc --exe --build -j

# 编译目标
.PHONY: all clean run view

all: $(TARGET)

$(TARGET): $(SV_SRC) $(CPP_SRC)
	$(VERILATOR) $(VERILATOR_FLAGS) $(SV_SRC) $(CPP_SRC) -o $(TARGET)

run: $(TARGET)
	./obj_dir/V$(TARGET)_sim

run_debug: $(TARGET)
	./obj_dir/V$(TARGET)_sim > sim_output.log 2>&1

view:
	gtkwave fpu_sim.fst

view_recent: run
	gtkwave fpu_sim.fst

clean:
	rm -rf obj_dir
	rm -f *.fst
	rm -f sim_output.log

#生成sv
chisel2sv:
	mill -i FP8fpu.runMain fpu.core.Fpu