#ifndef _KLEISLI_H_
#define _KLEISLI_H_

namespace category {
namespace kleisli {

template<typename T> T& The() { static T r; return r; }
template<typename T> T& The(const T& t) { static T r(t); return r; }
template<template<typename S> class R, typename S> R<S>& The(const S& s) { static R<S> r(s); return r; }
template<template<typename S, typename T> class R, typename S, typename T> R<S, T>& The(const T& t) { static R<S, T> r(t); return r; }
template<template<typename S, typename T, typename U> class R, typename S, typename T, typename U> R<S, T, U>& The(const U& u) { static R<S, T, U> r(u); return r; }

template<typename S>
struct end {
    virtual void next(const S&) = 0;
};

template<typename S>
struct empty: end<S> {
    void next(const S&) {}
};

template<typename V>
void operator>>=(const V& from, end<V>& to) {
    to.next(from);
}

template<typename Iter, typename V>
void operator>>=(const std::pair<Iter, Iter>& from, end<V>& to) {
    for (Iter it = from.first; it != from.second; to.next(*it++)) ;
}

template<typename Funct, typename V>
void operator>>=(const std::pair<Funct, int>& from, end<V>& to) {
    for (int i = 0; i < from.second; ++i) {
        to.next(from.first());
    }
}

template<typename T>
class sink {
    end<T>* _continuation;
protected:
    end<T>& continuation() const { return *_continuation; }
    void set_continuation(end<T>& c) { _continuation = &c; }
public:
    sink(): _continuation(&The< empty<T> >()) {}
    void pass(const T& t) {
        _continuation->next(t);
    }
};

template<typename S, typename T>
struct arr: sink<T>, end<S> {
    void next(const T& t) {
        pass(t);
    }
    end<S>& operator>>=(end<T>& to) {
        set_continuation(to);
        return *this;
    }
};

template<typename V, typename Iter>
class iterator_end: public end<V> {
    Iter it;
public:
    iterator_end(const Iter& i): it(i) {}
    void next(const V& value) { *it++ = value; }
};

}}

#endif
