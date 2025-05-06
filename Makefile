# Makefile for FP8 FPU simulation

# 源文件和目标
SV_SRC = src/main/resources/Top.sv
CPP_SRC = src/test/csrc/testacc.cpp
TARGET = top_sim

# Verilator配置
VERILATOR = verilator
VERILATOR_FLAGS = --trace-fst  # 使用FST格式而不是VCD
VERILATOR_FLAGS += --trace
VERILATOR_FLAGS += --cc
VERILATOR_FLAGS += --exe
VERILATOR_FLAGS += --build
VERILATOR_FLAGS += -j
VERILATOR_FLAGS += --timing  # 可选：添加时序检查

# 编译目标
.PHONY: all clean run view

all: $(TARGET)

$(TARGET): $(SV_SRC) $(CPP_SRC)
	$(VERILATOR) $(VERILATOR_FLAGS) $(SV_SRC) $(CPP_SRC) -o $(TARGET)

run: $(TARGET)
	./obj_dir/$(TARGET)

run_debug: $(TARGET)
	./obj_dir/$(TARGET) > sim_output.log 2>&1

# 使用gtkwave查看FST文件
view:
	gtkwave dump.fst setting.gtkw

view_recent: run
	gtkwave dump.fst

clean:
	rm -rf obj_dir
	rm -f *.fst  # 清理FST文件而不是VCD
	rm -f sim_output.log

# 生成sv
chisel2sv:
	mill -i FP8fpu.runMain fpu.core.Top