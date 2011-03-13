#ifndef _LOGGER_H_
#define _LOGGER_H_

#include <iostream>
#include <string>

class logger {
    static logger* std_logger;
public:
    virtual void operator()(const std::string& msg) = 0;
    virtual std::ostream& stream() const = 0;
    static void std(const std::string& msg) { (*std_logger)(msg); }
    static logger& std() { return *std_logger; }
    static void set_std(logger* l) { std_logger = l; }
    static std::ostream& std_stream() { return std_logger->stream(); }
};

class stderr_logger: public logger {
    std::string prepend;
public:
    stderr_logger(const std::string& p): prepend(p + ": ") {}
    void operator()(const std::string& msg) { std::cerr << prepend << msg << std::endl; }
    std::ostream& stream() const { return std::cerr << prepend; }
};

#endif
