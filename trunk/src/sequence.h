#ifndef _SEQUENCE_H_
#define _SEQUENCE_H_

template<typename S>
struct end {
    virtual void operator()(const S& v) = 0;
};

template<typename V>
void operator>>=(const V& from, end<V>& to) {
    to(from);
}

template<typename Iter, typename V>
void operator>>=(const std::pair<Iter, Iter>& from, end<V>& to) {
    for (Iter it = from.first; it != from.second; to(*it++)) ;
}

template<typename Funct, typename V>
void operator>>=(const std::pair<Funct, int>& from, end<V>& to) {
    for (int i = 0; i < from.second; ++i) {
        to(from.first());
    }
}

template<typename S, typename T>
class seq: public end<S> {
    end<T>* e;
protected:
    void put(const T& t) {
        (*e)(t);
    }
public:
    end<S>& operator>>=(end<T>& to) {
        e = &to;
        return *this;
    }
};

template<typename V, typename Iter>
class iterator_end: public end<V> {
    Iter it;
public:
    iterator_end(const Iter& i): it(i) {}
    void operator()(const V& value) { *it++ = value; }
};

template<typename T, typename P>
class filter: public seq<T, T> {
    P _p;
public:
    filter(const P& p): _p(p) {}
    void operator()(const T& value) {
        if (_p(value)) {
            put(value);
        }
    }
};

template<typename T>
struct id: seq<T, T> {
    void operator()(const T& t) { put(t); }
};

template<typename T> T& The() { static T r; return r; }
template<typename T> T& The(const T& t) { static T r(t); return r; }
template<template<typename S> class R, typename S> R<S>& The() { static R<S> r; return r; }
template<template<typename S> class R, typename S> R<S>& The(const S& s) { static R<S> r(s); return r; }
template<template<typename S> class R, typename S> R<S>& The(const R<S>& t) { static R<S> r(t); return r; }
template<template<typename S, typename T> class R, typename S, typename T> R<S, T>& The() { static R<S, T> r; return r; }
template<template<typename S, typename T> class R, typename S, typename T> R<S, T>& The(const T& t) { static R<S, T> r(t); return r; }
template<template<typename S, typename T> class R, typename S, typename T> R<S, T>& The(const R<S, T>& t) { static R<S, T> r(t); return r; }
template<template<typename S, typename T, typename U> class R, typename S, typename T, typename U> R<S, T, U>& The() { static R<S, T, U> r; return r; }
template<template<typename S, typename T, typename U> class R, typename S, typename T, typename U> R<S, T, U>& The(const U& u) { static R<S, T, U> r(u); return r; }
template<template<typename S, typename T, typename U> class R, typename S, typename T, typename U> R<S, T, U>& The(const R<S, T, U>& t) { static R<S, T, U> r(t); return r; }

#endif
