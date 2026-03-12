package mutate4java.cli;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.engine.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.util.List;

final class JavaFileArgumentValidator {

    void validate(List<String> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("mutate4java requires exactly one Java file");
        }
        if (values.size() != 1) {
            throw new IllegalArgumentException("mutate4java accepts exactly one Java file");
        }
        if (!values.get(0).endsWith(".java")) {
            throw new IllegalArgumentException("mutate4java target must be a .java file");
        }
    }
}
