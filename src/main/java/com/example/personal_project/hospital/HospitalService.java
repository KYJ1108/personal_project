package com.example.personal_project.hospital;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private HospitalRepository hospitalRepository;

    public List<Hospital> findAll(){
        return hospitalRepository.findAll();
    }

    public Hospital findById(Long id){
        return hospitalRepository.findById(id).orElse(null);
    }

    public Hospital save(Hospital hospital){
        return hospitalRepository.save(hospital);
    }

    public void deleteById(Long id){
        hospitalRepository.deleteById(id);
    }
}
