#ifndef _CLUSTERIZATION_H_
#define _CLUSTERIZATION_H_

#include <boost/shared_ptr.hpp>
#include <typeinfo>
#include <map>

#include "kleisli.h"
#include "file_types/base.h"

class clusterization: public category::kleisli::arr
  <boost::shared_ptr<file_type::base>, boost::shared_ptr<file_type::base> > {
    typedef boost::shared_ptr<file_type::base> T_ptr;
    typedef boost::shared_ptr<category::kleisli::end<T_ptr> > Cont;
    struct less {
        bool operator()(const T_ptr& t1, const T_ptr& t2) {
            if (typeid(*t1).before(typeid(*t2))) {
                return true;
            }
            if (typeid(*t2).before(typeid(*t1))) {
                return false;
            }
            return t1->precompare(t2) == file_type::less;
        }
    };
    std::map<T_ptr, Cont, less> _continuations;
public:
    void next(const T_ptr& t);
    void stop();
};

#endif
