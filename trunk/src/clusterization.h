#ifndef _CLUSTERIZATION_H_
#define _CLUSTERIZATION_H_

#include "kleisli.h"

template<typename T>
class clusterization: public list_arr<T, T> {
public:
    void next(const T& t) {
        (*this)(t);
    }
};

#endif
