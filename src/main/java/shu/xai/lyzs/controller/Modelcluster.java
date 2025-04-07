package shu.xai.lyzs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import shu.xai.lyzs.entity.ModelUpdateRequest;
import shu.xai.lyzs.entity.d_model_cluster;
import shu.xai.lyzs.entity.modelSelect;
import shu.xai.lyzs.service.ClusterDataService;
import shu.xai.lyzs.service.dataClusterService;

import java.util.List;

@RestController
@RequestMapping("/dmodel")
public class Modelcluster {
    @Autowired(required = false)
    ClusterDataService clusterDataService;
    @GetMapping("/clusterData")
    public List<d_model_cluster> getClusterData(@RequestParam String category, @RequestParam String clusterAlgorithmValue,@RequestParam String selectedCluster) {

        return clusterDataService.getCluster_data(category,clusterAlgorithmValue,selectedCluster);
    }
@PostMapping("/update-model")
public String updateModel(@RequestParam String category, @RequestParam String clusterAlgorithmValue,@RequestBody modelSelect request) {
    System.out.println(request.getModelName());
    System.out.println(request.getFitness());
    try {
        clusterDataService.updateModel(category,clusterAlgorithmValue,request.getId(), request.getModelName(), request.getFitness());
        return "Model updated successfully";
    } catch (Exception e) {
        return "Error updating model: " + e.getMessage();
    }
    }
}
