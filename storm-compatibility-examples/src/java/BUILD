package(default_visibility = ["//visibility:public"])

java_binary(
    name='heron-storm-compatibility-examples-unshaded',
    srcs = glob(["**/*.java"]),
    deps = [
        "//heron/api/src/java:api-java",
        "//heron/common/src/java:basics-java",
        "//storm-compatibility/src/java:storm-compatibility-java",
    ],
    create_executable = 0,
)

genrule(
    name = 'heron-storm-compatibility-examples',
    srcs = [":heron-storm-compatibility-examples-unshaded_deploy.jar"],
    outs = ["heron-storm-compatibility-examples.jar"],
    cmd  = "cp $< $@",
)
