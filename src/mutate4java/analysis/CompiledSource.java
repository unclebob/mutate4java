package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

import java.util.List;

record CompiledSource(List<CompilationUnitTree> units, Trees trees) {
}
