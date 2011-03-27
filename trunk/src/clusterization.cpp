#include "clusterization.h"

void clusterization::next(const T_ptr& t) {
    std::map<T_ptr, Cont, less>::iterator it = _continuations.find(t);
    if (it == _continuations.end()) {
        Cont cont = category::kleisli::sink<T_ptr>::continuation().clone();
        _continuations.insert(std::make_pair(t, cont));
        cont->next(t);
    } else {
        it->second->next(t);
    }
}

void clusterization::stop() {
    std::map<T_ptr, Cont, less>::iterator it = _continuations.begin();
    for (; it != _continuations.end(); ++it) {
        it->second->stop();
    }
    _continuations.clear();
}
