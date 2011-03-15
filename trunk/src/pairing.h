#ifndef _PAIRING_H_
#define _PAIRING_H_

#include <vector>

#include "kleisli.h"

template<typename T>
class pairing: public category::kleisli::arr<T, std::pair<T, T> > {
    std::vector<T> values;
public:
    void next(const T& t) {
        for (typename std::vector<T>::iterator it = values.begin(); it != values.end(); ++it) {
            (*this)(*it, t);
        }
        values.push_back(t);
    }
};

#endif
